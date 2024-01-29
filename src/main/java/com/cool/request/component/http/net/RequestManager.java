package com.cool.request.component.http.net;

import com.cool.request.common.bean.BeanInvokeSetting;
import com.cool.request.common.bean.EmptyEnvironment;
import com.cool.request.common.bean.RequestEnvironment;
import com.cool.request.common.bean.components.DynamicComponent;
import com.cool.request.common.bean.components.StaticComponent;
import com.cool.request.common.bean.components.controller.Controller;
import com.cool.request.common.bean.components.controller.DynamicController;
import com.cool.request.common.constant.CoolRequestConfigConstant;
import com.cool.request.common.constant.CoolRequestIdeaTopic;
import com.cool.request.common.exception.RequestParamException;
import com.cool.request.common.model.ErrorInvokeResponseModel;
import com.cool.request.common.model.InvokeResponseModel;
import com.cool.request.component.http.invoke.InvokeTimeoutException;
import com.cool.request.component.http.net.request.DynamicReflexHttpRequestParam;
import com.cool.request.component.http.net.request.StandardHttpRequestParam;
import com.cool.request.component.http.script.CompilationException;
import com.cool.request.component.http.script.JavaCodeEngine;
import com.cool.request.component.http.script.Request;
import com.cool.request.component.http.script.ScriptSimpleLogImpl;
import com.cool.request.lib.springmvc.RequestCache;
import com.cool.request.utils.MessagesWrapperUtils;
import com.cool.request.utils.NotifyUtils;
import com.cool.request.utils.ResourceBundleUtils;
import com.cool.request.utils.StringUtils;
import com.cool.request.utils.param.HTTPParameterProvider;
import com.cool.request.utils.param.PanelParameterProvider;
import com.cool.request.view.main.IRequestParamManager;
import com.cool.request.view.main.RequestEnvironmentProvide;
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
        defaultExceptionHandler = e -> NotifyUtils.notification(project, "Request Fail");
        exceptionHandler.put(InvokeTimeoutException.class, e -> NotifyUtils.notification(project, "Invoke Timeout"));
        exceptionHandler.put(RequestParamException.class, e -> MessagesWrapperUtils.showErrorDialog(e.getMessage(), "Tip"));
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

    private RequestContext createRequestContext() {
        return new RequestContext();
    }

    /**
     * 发送请求
     *
     * @return 如果发送成功则返回true，异步
     */
    public boolean sendRequest(Controller controller) {
        //如果没有选择节点，则停止
        if (controller == null) {
            NotifyUtils.notification(project, "Please Select a Node");
            return false;
        }
        if (activeHttpRequestIds.contains(controller.getId())) return false;//阻止重复点击
        activeHttpRequestIds.add(controller.getId());
        //需要确保开启子线程发送请求时后，waitResponseThread在下次点击时候必须存在，防止重复
        if (waitResponseThread.containsKey(controller.getId())) {
            MessagesWrapperUtils.showErrorDialog("Unable to execute, waiting for the previous task to end", "Tip");
            return false;
        }
        RequestEnvironment selectRequestEnvironment = Objects.requireNonNull(project.getUserData(CoolRequestConfigConstant.RequestEnvironmentProvideKey)).getSelectRequestEnvironment();
        //如果选择了反射调用，但是是静态数据，则停止
        if (requestParamManager.getInvokeModelIndex() == 1 && controller instanceof StaticComponent) {
            MessagesWrapperUtils.showErrorDialog(ResourceBundleUtils.getString("static.request.err"), ResourceBundleUtils.getString("tip"));
            return false;
        }

        //使用用户输入的url和method
        String url = requestParamManager.getUrl();
        if (!(selectRequestEnvironment instanceof EmptyEnvironment) && requestParamManager.getInvokeModelIndex() == 1) {
            url = StringUtils.joinUrlPath("http://localhost:" + controller.getServerPort(), StringUtils.removeHostFromUrl(url));
        }
        BeanInvokeSetting beanInvokeSetting = requestParamManager.getBeanInvokeSetting();
        //创建请求参数对象
        StandardHttpRequestParam standardHttpRequestParam = requestParamManager.getInvokeModelIndex() == 1 ?
                new DynamicReflexHttpRequestParam(beanInvokeSetting.isUseProxy(),
                        beanInvokeSetting.isUseInterceptor(),
                        false, ((DynamicController) controller)) :
                new StandardHttpRequestParam();
        standardHttpRequestParam.setId(controller.getId());
        //应用参数从参数面板和全局变量
        HTTPParameterProvider panelParameterProvider = new PanelParameterProvider();
        requestParamManager.postApplyParam(standardHttpRequestParam);

        standardHttpRequestParam.getHeaders().addAll(panelParameterProvider.getHeader(project, controller, selectRequestEnvironment));
        standardHttpRequestParam.getUrlParam().addAll(panelParameterProvider.getUrlParam(project, controller, selectRequestEnvironment));
        standardHttpRequestParam.setBody(panelParameterProvider.getBody(project, controller, selectRequestEnvironment));
        standardHttpRequestParam.setUrl(url);
        standardHttpRequestParam.setMethod(requestParamManager.getHttpMethod());

        //选择调用方式
        //保存缓存
        RequestCache requestCache = RequestCache.RequestCacheBuilder.aRequestCache()
                .withHttpMethod(requestParamManager.getHttpMethod().toString())
                .withHeaders(requestParamManager.getHttpHeader())
                .withUrlParams(requestParamManager.getUrlParam())
                .withRequestBodyType(requestParamManager.getRequestBodyType().getValue())
                .withFormDataInfos(requestParamManager.getFormData())
                .withUrlencodedBody(requestParamManager.getUrlencodedBody())
                .withRequestBody(requestParamManager.getRequestBody())
                .withUrl(url)
                .withPort(controller.getServerPort())
                .withScriptLog("")
                .withRequestScript(requestParamManager.getRequestScript())
                .withResponseScript(requestParamManager.getResponseScript())
                .withContentPath(controller.getContextPath())
                .withUseProxy(beanInvokeSetting.isUseProxy())
                .withUseInterceptor(beanInvokeSetting.isUseInterceptor())
                .withInvokeModelIndex(requestParamManager.getInvokeModelIndex())
                .build();
        RequestParamCacheManager.setCache(controller.getId(), requestCache);

        //检查url
        if (!checkUrl(url)) {
            NotifyUtils.notification(project, "Invalid URL");
            return false;
        }
        //请求发送开始通知
        project.getMessageBus().syncPublisher(CoolRequestIdeaTopic.REQUEST_SEND_BEGIN).event(controller);
        ProgressManager.getInstance().run(new HTTPRequestTaskBackgroundable(project, controller, standardHttpRequestParam, requestCache));
        return false;
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
            super(project, "Init");
            this.project = project;
            this.controller = controller;
            this.standardHttpRequestParam = standardHttpRequestParam;
            this.requestCache = requestCache;
        }

        @Override
        public void run(@NotNull ProgressIndicator indicator) {
            try {
                JavaCodeEngine javaCodeEngine = new JavaCodeEngine(project);
                //执行脚本
                ScriptSimpleLogImpl scriptSimpleLog = new ScriptSimpleLogImpl(project, controller.getId());
                scriptSimpleLog.clearLog();
                boolean canRequest = false;
                try {
                    indicator.setText("Execute script");
                    indicator.setFraction(0.8);
                    canRequest = javaCodeEngine.execRequest(new Request(standardHttpRequestParam), requestCache.getRequestScript(), scriptSimpleLog);
                } catch (Exception e) {
                    MessagesWrapperUtils.showErrorDialog(e.getMessage(),
                            e instanceof CompilationException ?
                                    "Request Script Syntax Error ,Please Check!" : "Request Script Run Error");
                    //脚本出现异常后停止
                    throw e;
                }
                //脚本没拦截本次请求
                if (canRequest) {
                    BasicControllerRequestCallMethod basicRequestCallMethod = getBaseRequest(standardHttpRequestParam, controller);
                    indicator.setFraction(0.9);
                    //发送请求
                    if (!runHttpRequestTask(controller, basicRequestCallMethod, indicator)) {
                        MessagesWrapperUtils.showErrorDialog("Unable to execute, waiting for the previous task to end", "Tip");
                    }
                } else {
                    MessagesWrapperUtils.showInfoMessage(ResourceBundleUtils.getString("http.request.rejected"), "Tip");
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
                invokeResponseModel.setData(new byte[]{0});
                if (response.body() != null) {
                    try {
                        invokeResponseModel.setData(response.body().bytes());
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
        int startPort = 0;
        if (controller instanceof DynamicComponent) {
            startPort = ((DynamicComponent) controller).getSpringBootStartPort();
        }
        return requestParamManager.getInvokeModelIndex() == 1 ?
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
            waitResponseThread.put(invokeId, thread);
            Objects.requireNonNull(project.getUserData(CoolRequestConfigConstant.RequestContextManagerKey)).put(invokeId, createRequestContext());
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
