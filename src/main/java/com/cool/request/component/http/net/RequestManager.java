package com.cool.request.component.http.net;

import com.cool.request.common.bean.BeanInvokeSetting;
import com.cool.request.common.bean.EmptyEnvironment;
import com.cool.request.common.bean.RequestEnvironment;
import com.cool.request.common.bean.components.DynamicComponent;
import com.cool.request.common.bean.components.StaticComponent;
import com.cool.request.common.bean.components.controller.Controller;
import com.cool.request.common.bean.components.controller.CustomController;
import com.cool.request.common.bean.components.controller.DynamicController;
import com.cool.request.common.bean.components.controller.TemporaryController;
import com.cool.request.common.constant.CoolRequestConfigConstant;
import com.cool.request.common.constant.CoolRequestIdeaTopic;
import com.cool.request.common.exception.RequestParamException;
import com.cool.request.common.model.ErrorInvokeResponseModel;
import com.cool.request.common.model.InvokeResponseModel;
import com.cool.request.component.http.invoke.InvokeTimeoutException;
import com.cool.request.component.http.net.request.DynamicReflexHttpRequestParam;
import com.cool.request.component.http.net.request.HttpRequestParamUtils;
import com.cool.request.component.http.net.request.StandardHttpRequestParam;
import com.cool.request.component.http.script.CompilationException;
import com.cool.request.component.http.script.JavaCodeEngine;
import com.cool.request.component.http.script.Request;
import com.cool.request.component.http.script.ScriptSimpleLogImpl;
import com.cool.request.lib.springmvc.EmptyBody;
import com.cool.request.lib.springmvc.RequestCache;
import com.cool.request.utils.*;
import com.cool.request.utils.param.HTTPParameterProvider;
import com.cool.request.utils.param.PanelParameterProvider;
import com.cool.request.utils.url.UriComponentsBuilder;
import com.cool.request.view.main.IRequestParamManager;
import com.cool.request.view.tool.Provider;
import com.cool.request.view.tool.ProviderManager;
import com.cool.request.view.tool.RequestParamCacheManager;
import com.cool.request.view.tool.UserProjectManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import okhttp3.Headers;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.LockSupport;
import java.util.function.Consumer;

public class RequestManager implements Provider {
    private static final Logger LOG = Logger.getInstance(RequestManager.class);
    private final IRequestParamManager requestParamManager;
    private final Project project;
    private final UserProjectManager userProjectManager;
    private final Map<String, Thread> waitResponseThread = new ConcurrentHashMap<>();
    private final Map<Class<? extends Exception>, Consumer<Exception>> exceptionHandler = new HashMap<>();
    private final Consumer<Exception> defaultExceptionHandler;
    private final List<String> activeHttpRequestIds = new ArrayList<>();

    public RequestManager(IRequestParamManager requestParamManager,
                          Project project,
                          UserProjectManager userProjectManager) {
        this.requestParamManager = requestParamManager;
        this.project = project;
        this.userProjectManager = userProjectManager;
        defaultExceptionHandler = e -> NotifyUtils.notification(project, "Request Fail" + e.getMessage());
        exceptionHandler.put(InvokeTimeoutException.class, e -> NotifyUtils.notification(project, "Invoke Timeout"));
        exceptionHandler.put(RequestParamException.class, e -> MessagesWrapperUtils.showErrorDialog(e.getMessage(), ResourceBundleUtils.getString("tip")));
        ProviderManager.registerProvider(RequestManager.class, CoolRequestConfigConstant.RequestManagerKey, this, project);
        project.getMessageBus().connect().subscribe(CoolRequestIdeaTopic.HTTP_RESPONSE, (CoolRequestIdeaTopic.HttpResponseEventListener) (requestId, invokeResponseModel) -> {
            cancelHttpRequest(requestId);
            JavaCodeEngine javaCodeEngine = new JavaCodeEngine(project);
            RequestCache requestCache = RequestParamCacheManager.getCache(requestId);
            if (requestCache != null) {
                ScriptSimpleLogImpl scriptSimpleLog = new ScriptSimpleLogImpl(project, requestId);
                javaCodeEngine.execResponse(new com.cool.request.component.http.script.Response(invokeResponseModel), requestCache.getResponseScript(), scriptSimpleLog);
            }
        });
    }

    private RequestContext createRequestContext(Controller controller) {
        return new RequestContext(controller);
    }

    /**
     * 预检测
     */
    private boolean preRequest(Controller controller) {
        if (controller == null) {
            NotifyUtils.notification(project, ResourceBundleUtils.getString("please.select.node"));
            return false;
        }
        //检查url
        if (!checkUrl(requestParamManager.getUrl())) {
            NotifyUtils.notification(project, ResourceBundleUtils.getString("invalid.url"));
            return false;
        }
        return true;
    }

    private String generatorRequestURL(RequestEnvironment selectRequestEnvironment, Controller controller) {
        String url = requestParamManager.getUrl();
        //如果选择了环境，并且选择了反射调用，则恢复到默认地址
        if (!(selectRequestEnvironment instanceof EmptyEnvironment) && requestParamManager.isReflexRequest()) {
            if (!(controller instanceof CustomController)) {
                url = StringUtils.joinUrlPath("http://localhost:" + controller.getServerPort(), StringUtils.removeHostFromUrl(url));
            }
        }
        return url;
    }

    /**
     * 发送请求
     *
     * @return 如果发送成功则返回true，异步
     */
    public boolean sendRequest(Controller controller) {
        //如果没有选择节点，则停止
        if (!preRequest(controller)) return false;
        try {
            //需要确保开启子线程发送请求时后，waitResponseThread在下次点击时候必须存在，防止重复
            if (waitResponseThread.containsKey(controller.getId())) {
                MessagesWrapperUtils.showErrorDialog("Unable to execute, waiting for the previous task to end", ResourceBundleUtils.getString("tip"));
                return false;
            }
            RequestEnvironment selectRequestEnvironment = Objects.requireNonNull(project.getUserData(CoolRequestConfigConstant.RequestEnvironmentProvideKey)).getSelectRequestEnvironment();
            //如果选择了反射调用，但是是静态数据，则停止
            if (requestParamManager.isReflexRequest() && controller instanceof StaticComponent) {
                MessagesWrapperUtils.showErrorDialog(ResourceBundleUtils.getString("static.request.err"), ResourceBundleUtils.getString("tip"));
                return false;
            }
            if (activeHttpRequestIds.contains(controller.getId())) return false;//阻止重复点击
            activeHttpRequestIds.add(controller.getId());
            //使用用户输入的url和method
            String url = generatorRequestURL(selectRequestEnvironment, controller);
            //创建请求参数对象
            StandardHttpRequestParam standardHttpRequestParam = createStandardHttpRequestParam(controller);
            standardHttpRequestParam.setId(controller.getId());
            //应用参数从参数面板和全局变量
            HTTPParameterProvider panelParameterProvider = new PanelParameterProvider();

            //设置参数
            standardHttpRequestParam.getHeaders().addAll(panelParameterProvider.getHeader(project, controller, selectRequestEnvironment));
            standardHttpRequestParam.getUrlParam().addAll(panelParameterProvider.getUrlParam(project, controller, selectRequestEnvironment));
            standardHttpRequestParam.setBody(panelParameterProvider.getBody(project, controller, selectRequestEnvironment));
            standardHttpRequestParam.setMethod(requestParamManager.getHttpMethod());
            standardHttpRequestParam.getPathParam().addAll(panelParameterProvider.getPathParam(project));

            //拼接全局参数
            for (KeyValue keyValue : standardHttpRequestParam.getUrlParam()) {
                url = HttpRequestParamUtils.addParameterToUrl(url, keyValue.getKey(), keyValue.getValue());
            }
            //构建url path参数
            List<KeyValue> pathParam = requestParamManager.getPathParam();
            Map<String, String> pathParamMap = new HashMap<>();
            for (KeyValue keyValue : pathParam) {
                pathParamMap.put(keyValue.getKey(), keyValue.getValue());
            }
            try {
                url = UriComponentsBuilder.fromHttpUrl(url)
                        .buildAndExpand(pathParamMap)
                        .toUriString();
            } catch (Exception e) {
                MessagesWrapperUtils.showErrorDialog(e.getMessage(), ResourceBundleUtils.getString("tip"));
                throw e;
            }
            //如果用户没有设置ContentType,则更具请求体来设置
            String contentType = HttpRequestParamUtils.getContentType(standardHttpRequestParam, null);
            if (contentType == null && standardHttpRequestParam.getBody() != null) {
                if (!(standardHttpRequestParam.getBody() instanceof EmptyBody)) {
                    HttpRequestParamUtils.setContentType(standardHttpRequestParam, standardHttpRequestParam.getBody().getMediaType());
                }
            }
            standardHttpRequestParam.setUrl(url);
            //处理临时请求，其他保存参数缓存
            RequestCache requestCache = requestParamManager.createRequestCache();
            if (!(controller instanceof TemporaryController)) {
                RequestParamCacheManager.setCache(controller.getId(), requestCache);
            }
            //请求发送开始通知
            project.getMessageBus().syncPublisher(CoolRequestIdeaTopic.REQUEST_SEND_BEGIN).event(controller);
            ProgressManager.getInstance().run(new HTTPRequestTaskBackgroundable(project, controller, standardHttpRequestParam, requestCache));
        } catch (Exception e) {
            activeHttpRequestIds.remove(controller.getId());
        }
        return false;
    }

    private StandardHttpRequestParam createStandardHttpRequestParam(Controller controller) {
        BeanInvokeSetting beanInvokeSetting = requestParamManager.getBeanInvokeSetting();
        if (controller instanceof CustomController) return new StandardHttpRequestParam();

        return requestParamManager.isReflexRequest() ?
                new DynamicReflexHttpRequestParam(beanInvokeSetting.isUseProxy(),
                        beanInvokeSetting.isUseInterceptor(),
                        false, ((DynamicController) controller)) :
                new StandardHttpRequestParam();
    }

    public boolean exist(String id) {
        return waitResponseThread.containsKey(id);
    }

    class HTTPRequestTaskBackgroundable extends Task.Backgroundable {
        private final Project project;
        private final Controller controller;
        private final StandardHttpRequestParam standardHttpRequestParam;
        private final RequestCache requestCache;

        public HTTPRequestTaskBackgroundable(Project project, Controller controller,
                                             StandardHttpRequestParam standardHttpRequestParam,
                                             RequestCache requestCache) {
            super(project, "Send Request");
            this.project = project;
            this.controller = controller;
            this.standardHttpRequestParam = standardHttpRequestParam;
            this.requestCache = requestCache;
        }

        @Override
        public void run(@NotNull ProgressIndicator indicator) {
            try {
                //创建请求上下文，请求执行阶段可能会产生额外数据，都通过createRequestContext来中转
                Objects.requireNonNull(project.getUserData(CoolRequestConfigConstant.RequestContextManagerKey))
                        .put(controller.getId(), createRequestContext(controller));

                JavaCodeEngine javaCodeEngine = new JavaCodeEngine(project);
                //执行脚本，ScriptSimpleLogImpl是脚本中日志输出的实现
                ScriptSimpleLogImpl scriptSimpleLog = new ScriptSimpleLogImpl(project, controller.getId());
                scriptSimpleLog.clearLog();
                boolean canRequest = false;
                try {
                    indicator.setText("Execute script");
                    indicator.setFraction(0.8);
                    canRequest = javaCodeEngine.execRequest(new Request(standardHttpRequestParam, scriptSimpleLog),
                            requestCache.getRequestScript(), scriptSimpleLog);
                } catch (Exception e) {
                    //脚本编写不对可能出现异常，请求也同时停止
                    e.printStackTrace(scriptSimpleLog);
                    MessagesWrapperUtils.showErrorDialog(e.getMessage(),
                            e instanceof CompilationException ?
                                    "Request Script Syntax Error ,Please Check!" : "Request Script Run Error");
                    //脚本出现异常后停止
                    throw e;
                }
                //脚本没拦截本次请求，用户返回了true
                if (canRequest) {
                    BasicControllerRequestCallMethod basicRequestCallMethod = getBaseRequest(standardHttpRequestParam, controller);
                    indicator.setFraction(0.9);
                    //发送请求，上一个相同请求可能被发起，则停止
                    if (!runHttpRequestTask(controller, basicRequestCallMethod, indicator)) {
                        MessagesWrapperUtils.showErrorDialog("Unable to execute, waiting for the previous task to end", ResourceBundleUtils.getString("tip"));
                    }
                } else {
                    MessagesWrapperUtils.showInfoMessage(ResourceBundleUtils.getString("http.request.rejected"), ResourceBundleUtils.getString("tip"));
                    cancelHttpRequest(controller.getId());
                }
            } catch (Exception e) {
                cancelHttpRequest(controller.getId());
                exceptionHandler.getOrDefault(e.getClass(), defaultExceptionHandler).accept(e);
                project.getMessageBus().syncPublisher(CoolRequestIdeaTopic.REQUEST_SEND_END).event(controller);

            }
        }
    }

    private boolean runHttpRequestTask(Controller controller,
                                       BasicControllerRequestCallMethod basicRequestCallMethod,
                                       @NotNull ProgressIndicator indicator) throws Exception {
        String invokeId = controller.getId();
        if (waitResponseThread.containsKey(invokeId)) {
            return false;
        }
        Thread thread = Thread.currentThread();
        waitResponseThread.put(invokeId, thread);
        HttpRequestTaskExecute httpRequestTask = new HttpRequestTaskExecute(controller, basicRequestCallMethod);
        httpRequestTask.run(indicator);
        return true;
    }

    public void cancelHttpRequest(String requestId) {
        activeHttpRequestIds.remove(requestId);
        Thread thread = waitResponseThread.get(requestId);
        if (thread != null) {
            LockSupport.unpark(thread);
            waitResponseThread.remove(requestId);
        }
        project.getMessageBus().syncPublisher(CoolRequestIdeaTopic.REQUEST_SEND_END).event(requestId);

    }

    private BasicControllerRequestCallMethod getBaseRequest(StandardHttpRequestParam standardHttpRequestParam,
                                                            Controller controller) {
        HttpRequestCallMethod.SimpleCallback simpleCallback = new HttpRequestCallMethod.SimpleCallback() {
            @Override
            public void onResponse(String requestId, int code, Response response) {
                if (!waitResponseThread.containsKey(requestId)) {
                    return;
                }

                Headers okHttpHeaders = response.headers();
                List<InvokeResponseModel.Header> headers = new ArrayList<>();
                int headerCount = okHttpHeaders.size();
                for (int i = 0; i < headerCount; i++) {
                    String headerName = okHttpHeaders.name(i);
                    String headerValue = okHttpHeaders.value(i);
                    headers.add(new InvokeResponseModel.Header(headerName, headerValue));
                }
                InvokeResponseModel invokeResponseModel = new InvokeResponseModel();
                invokeResponseModel.setBase64BodyData("");
                if (response.body() != null) {
                    try {
                        invokeResponseModel.setBase64BodyData(Base64Utils.encodeToString(response.body().bytes()));
                    } catch (IOException ignored) {
                    }
                }
                invokeResponseModel.setCode(response.code());
                invokeResponseModel.setId(requestId);
                invokeResponseModel.setHeader(headers);
                project.getMessageBus().syncPublisher(CoolRequestIdeaTopic.HTTP_RESPONSE).onResponseEvent(requestId, invokeResponseModel);
            }

            @Override
            public void onError(String requestId, IOException e) {
                project.getMessageBus()
                        .syncPublisher(CoolRequestIdeaTopic.HTTP_RESPONSE)
                        .onResponseEvent(requestId, new ErrorInvokeResponseModel(e.getMessage().getBytes()));
            }
        };
        if (controller instanceof CustomController) {
            return new HttpRequestCallMethod(standardHttpRequestParam, simpleCallback);
        }
        int startPort = 0;
        if (controller instanceof DynamicComponent) {
            startPort = ((DynamicComponent) controller).getSpringBootStartPort();
        }

        return requestParamManager.isReflexRequest() ?
                new ReflexRequestCallMethod(((DynamicReflexHttpRequestParam) standardHttpRequestParam), startPort, userProjectManager) :
                new HttpRequestCallMethod(standardHttpRequestParam, simpleCallback);
    }

    private boolean checkUrl(String url) {
        try {
            new URL(url);
            return true;
        } catch (MalformedURLException ignored) {
        }
        return false;
    }

    public void removeAllData() {
        waitResponseThread.clear();
        activeHttpRequestIds.clear();
    }

    public class HttpRequestTaskExecute {
        private final BasicControllerRequestCallMethod basicControllerRequestCallMethod;
        private final Controller controller;

        public HttpRequestTaskExecute(Controller controller,
                                      BasicControllerRequestCallMethod basicRequestCallMethod) {
            this.basicControllerRequestCallMethod = basicRequestCallMethod;
            this.controller = controller;
        }

        public void run(@NotNull ProgressIndicator indicator) throws Exception {
            String invokeId = controller.getId();
            Thread thread = Thread.currentThread();
            basicControllerRequestCallMethod.invoke();
            indicator.setText("Wait " + controller.getUrl() + " Response");
            while (!indicator.isCanceled() && waitResponseThread.containsKey(invokeId)) {
                LockSupport.parkNanos(thread, 500);
            }
            if (indicator.isCanceled()) {
                cancelHttpRequest(controller.getId());
            }

        }
    }

    public boolean canEnabledSendButton(String id) {
        return !waitResponseThread.containsKey(id);
    }
}
