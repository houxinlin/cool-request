package com.cool.request.net;

import com.cool.request.Constant;
import com.cool.request.IdeaTopic;
import com.cool.request.bean.BeanInvokeSetting;
import com.cool.request.bean.EmptyEnvironment;
import com.cool.request.bean.RequestEnvironment;
import com.cool.request.bean.components.DynamicComponent;
import com.cool.request.bean.components.StaticComponent;
import com.cool.request.bean.components.controller.Controller;
import com.cool.request.bean.components.controller.DynamicController;
import com.cool.request.cool.request.DynamicReflexHttpRequestParam;
import com.cool.request.invoke.InvokeTimeoutException;
import com.cool.request.model.ErrorInvokeResponseModel;
import com.cool.request.model.InvokeResponseModel;
import com.cool.request.net.request.StandardHttpRequestParam;
import com.cool.request.script.JavaCodeEngine;
import com.cool.request.script.Request;
import com.cool.request.script.ScriptSimpleLogImpl;
import com.cool.request.springmvc.RequestCache;
import com.cool.request.tool.ProviderManager;
import com.cool.request.utils.*;
import com.cool.request.utils.exception.RequestParamException;
import com.cool.request.view.IRequestParamManager;
import com.cool.request.view.main.RequestEnvironmentProvide;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import okhttp3.Headers;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.LockSupport;
import java.util.function.Consumer;

public class RequestManager {
    private static final Logger LOG = Logger.getInstance(RequestManager.class);
    private final IRequestParamManager requestParamManager;
    private final Project project;
    private final UserProjectManager userProjectManager;
    private final Map<String, Thread> waitResponseThread = new ConcurrentHashMap<>();
    private final Map<String, Boolean> buttonStateMap = new HashMap<>();
    private final Map<Class<? extends Exception>, Consumer<Exception>> exceptionHandler = new HashMap<>();
    private final Consumer<Exception> defaultExceptionHandler;

    public RequestManager(IRequestParamManager requestParamManager,
                          Project project,
                          UserProjectManager userProjectManager) {
        this.requestParamManager = requestParamManager;
        this.project = project;
        this.userProjectManager = userProjectManager;
        defaultExceptionHandler = e -> NotifyUtils.notification(project, "Request Fail");
        exceptionHandler.put(InvokeTimeoutException.class, e -> NotifyUtils.notification(project, "Invoke Timeout"));
        exceptionHandler.put(RequestParamException.class, e -> MessagesWrapperUtils.showErrorDialog(e.getMessage(), "Tip"));

        project.getMessageBus().connect().subscribe(IdeaTopic.HTTP_RESPONSE, (IdeaTopic.HttpResponseEventListener) (requestId, invokeResponseModel) -> {
            cancelHttpRequest(requestId);
            JavaCodeEngine javaCodeEngine = new JavaCodeEngine();
            RequestCache requestCache = RequestParamCacheManager.getCache(requestId);
            if (requestCache != null) {
                ScriptSimpleLogImpl scriptSimpleLog = new ScriptSimpleLogImpl(project, requestId);
                javaCodeEngine.execResponse(new com.cool.request.script.Response(invokeResponseModel), requestCache.getResponseScript(), scriptSimpleLog);
            }
        });
    }

    private RequestContext createRequestContext() {
        return new RequestContext();
    }

    public void sendRequest(Controller controller) {
        if (controller == null) {
            NotifyUtils.notification(project, "Please Select a Node");
            return;
        }
        RequestEnvironment selectRequestEnvironment = Objects.requireNonNull(project.getUserData(Constant.RequestEnvironmentProvideKey)).getSelectRequestEnvironment();
        if (!(selectRequestEnvironment instanceof EmptyEnvironment) && requestParamManager.getInvokeModelIndex() == 1) {
            Messages.showErrorDialog(ResourceBundleUtils.getString("env.not.emp.err"), ResourceBundleUtils.getString("tip"));
            return;
        }
        if (requestParamManager.getInvokeModelIndex() == 1 && controller instanceof StaticComponent) {
            Messages.showErrorDialog(ResourceBundleUtils.getString("static.request.err"), ResourceBundleUtils.getString("tip"));
            return;
        }
        //使用用户输入的url和method
        String url = requestParamManager.getUrl();
        BeanInvokeSetting beanInvokeSetting = requestParamManager.getBeanInvokeSetting();

        //创建请求参数对象
        StandardHttpRequestParam standardHttpRequestParam = requestParamManager.getInvokeModelIndex() == 1 ?
                new DynamicReflexHttpRequestParam(beanInvokeSetting.isUseProxy(),
                        beanInvokeSetting.isUseInterceptor(),
                        false, ((DynamicController) controller)) :
                new StandardHttpRequestParam();
        standardHttpRequestParam.setId(controller.getId());
        //应用全局变量
        requestParamManager.preApplyParam(standardHttpRequestParam);
        ProviderManager.findAndConsumerProvider(RequestEnvironmentProvide.class, project, requestEnvironmentProvide -> {
            requestEnvironmentProvide.applyEnvironmentParam(standardHttpRequestParam);
        });
        //设置请求参数
        requestParamManager.postApplyParam(standardHttpRequestParam);
        //选择调用方式
        //保存缓存
        RequestCache requestCache = RequestCache.RequestCacheBuilder.aRequestCache()
                .withHttpMethod(requestParamManager.getHttpMethod().toString())
                .withHeaders(requestParamManager.getHttpHeader())
                .withUrlParams(requestParamManager.getUrlParam())
                .withRequestBodyType(requestParamManager.getRequestBodyType())
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

        if (!checkUrl(url)) {
            NotifyUtils.notification(project, "Invalid URL");
            return;
        }
        JavaCodeEngine javaCodeEngine = new JavaCodeEngine();
        ScriptSimpleLogImpl scriptSimpleLog = new ScriptSimpleLogImpl(project, controller.getId());
        scriptSimpleLog.clearLog();
        if (javaCodeEngine.execRequest(new Request(standardHttpRequestParam), requestCache.getRequestScript(), scriptSimpleLog)) {
            BasicControllerRequestCallMethod basicRequestCallMethod = getBaseRequest(standardHttpRequestParam, controller);
            LOG.info(standardHttpRequestParam.toString());
            CountDownLatch countDownLatch = new CountDownLatch(1);
            if (!runNewHttpRequestProgressTask(controller, basicRequestCallMethod, countDownLatch)) {
                Messages.showErrorDialog("Unable to execute, waiting for the previous task to end", "Tip");
            } else {
                //有数据不安全行为，只能等这里执行完在让子任务运行
                requestParamManager.setSendButtonEnabled(false);
            }
            countDownLatch.countDown();
        }
    }

    private boolean runNewHttpRequestProgressTask(Controller controller,
                                                  BasicControllerRequestCallMethod basicRequestCallMethod,
                                                  CountDownLatch latch) {
        String invokeId = controller.getId();
        if (waitResponseThread.containsKey(invokeId)) {
            return false;
        }
        ProgressManager.getInstance().run(new HttpRequestTask(project, controller, basicRequestCallMethod, latch));

        buttonStateMap.put(controller.getId(), false);

        return true;
    }

    public void cancelHttpRequest(String requestId) {
        Thread thread = waitResponseThread.get(requestId);
        if (thread != null) {
            LockSupport.unpark(thread);
            waitResponseThread.remove(requestId);
        }
        buttonStateMap.remove(requestId);
        if (requestId.equalsIgnoreCase(requestParamManager.getCurrentController().getId())) {
            requestParamManager.setSendButtonEnabled(true);
        }
    }

    private BasicControllerRequestCallMethod getBaseRequest(StandardHttpRequestParam standardHttpRequestParam,
                                                            Controller controller) {
        HttpRequestCallMethod.SimpleCallback simpleCallback = new HttpRequestCallMethod.SimpleCallback() {
            @Override
            public void onResponse(String requestId, int code, Response response) {
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
                invokeResponseModel.setId(requestId);
                invokeResponseModel.setHeader(headers);
                project.getMessageBus().syncPublisher(IdeaTopic.HTTP_RESPONSE).onResponseEvent(requestId, invokeResponseModel);
            }

            @Override
            public void onError(String requestId, IOException e) {
                project.getMessageBus()
                        .syncPublisher(IdeaTopic.HTTP_RESPONSE)
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
        this.waitResponseThread.clear();
        buttonStateMap.clear();
    }

    private class HttpRequestTask extends Task.Backgroundable {
        private BasicControllerRequestCallMethod basicControllerRequestCallMethod;
        private CountDownLatch countDownLatch;
        private final Controller controller;

        public HttpRequestTask(@Nullable Project project, Controller controller,
                               BasicControllerRequestCallMethod basicRequestCallMethod,
                               CountDownLatch latch) {
            super(project, "Send Request" + controller.getUrl());
            this.basicControllerRequestCallMethod = basicRequestCallMethod;
            this.countDownLatch = latch;
            this.controller = controller;
        }


        @Override
        public void run(@NotNull ProgressIndicator indicator) {
            String invokeId = controller.getId();
            try {
                countDownLatch.await();
            } catch (InterruptedException ignored) {
            }
            Thread thread = Thread.currentThread();
            waitResponseThread.put(invokeId, thread);
            try {
                Objects.requireNonNull(project.getUserData(Constant.RequestContextManagerKey)).put(invokeId, createRequestContext());
                basicControllerRequestCallMethod.invoke();
                indicator.setText("Wait " + controller.getUrl() + " Response");
                while (!indicator.isCanceled() && waitResponseThread.containsKey(invokeId)) {
                    LockSupport.parkNanos(thread, 500);
                }
                if (indicator.isCanceled()) {
                    cancelHttpRequest(controller.getId());
                }
            } catch (Exception e) {
                e.printStackTrace();
                cancelHttpRequest(controller.getId());
                exceptionHandler.getOrDefault(e.getClass(), defaultExceptionHandler).accept(e);
            }
        }
    }

    public boolean canEnabledSendButton(String id) {
        return buttonStateMap.getOrDefault(id, true);
    }
}
