package com.hxl.plugin.springboot.invoke.net;

import com.hxl.plugin.springboot.invoke.Constant;
import com.hxl.plugin.springboot.invoke.IdeaTopic;
import com.hxl.plugin.springboot.invoke.bean.*;
import com.hxl.plugin.springboot.invoke.bean.components.DynamicComponent;
import com.hxl.plugin.springboot.invoke.bean.components.StaticComponent;
import com.hxl.plugin.springboot.invoke.bean.components.controller.Controller;
import com.hxl.plugin.springboot.invoke.bean.components.controller.DynamicController;
import com.hxl.plugin.springboot.invoke.cool.request.DynamicControllerRequestData;
import com.hxl.plugin.springboot.invoke.invoke.InvokeTimeoutException;
import com.hxl.plugin.springboot.invoke.model.ErrorInvokeResponseModel;
import com.hxl.plugin.springboot.invoke.model.InvokeResponseModel;
import com.hxl.plugin.springboot.invoke.net.request.ControllerRequestData;
import com.hxl.plugin.springboot.invoke.script.JavaCodeEngine;
import com.hxl.plugin.springboot.invoke.script.Request;
import com.hxl.plugin.springboot.invoke.script.ScriptSimpleLogImpl;
import com.hxl.plugin.springboot.invoke.springmvc.RequestCache;
import com.hxl.plugin.springboot.invoke.utils.NotifyUtils;
import com.hxl.plugin.springboot.invoke.utils.RequestParamCacheManager;
import com.hxl.plugin.springboot.invoke.utils.ResourceBundleUtils;
import com.hxl.plugin.springboot.invoke.utils.UserProjectManager;
import com.hxl.plugin.springboot.invoke.view.IRequestParamManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import okhttp3.Headers;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.LockSupport;

public class RequestManager {
    private static final Logger LOG = Logger.getInstance(RequestManager.class);
    private final IRequestParamManager requestParamManager;
    private final Project project;
    private final UserProjectManager userProjectManager;
    private final Map<String, Thread> waitResponseThread = new ConcurrentHashMap<>();
    private final Map<String, Boolean> buttonStateMap = new HashMap<>();
    private final ScriptSimpleLogImpl scriptSimpleLog;

    public RequestManager(IRequestParamManager requestParamManager,
                          Project project,
                          UserProjectManager userProjectManager) {
        this.requestParamManager = requestParamManager;
        this.project = project;
        this.userProjectManager = userProjectManager;
        this.scriptSimpleLog = new ScriptSimpleLogImpl(project);

        project.getMessageBus().connect().subscribe(IdeaTopic.HTTP_RESPONSE, (IdeaTopic.HttpResponseEventListener) (requestId, invokeResponseModel) -> {
            cancelHttpRequest(requestId);
            JavaCodeEngine javaCodeEngine = new JavaCodeEngine();
            RequestCache requestCache = RequestParamCacheManager.getCache(requestId);
            if (requestCache != null) {
//                javaCodeEngine.execResponse(new com.hxl.plugin.springboot.invoke.script.Response(invokeResponseModel),
//                        requestCache.getResponseScript(),
//                        userProjectManager.getScriptSimpleLog());
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
        RequestEnvironment selectRequestEnvironment = project.getUserData(Constant.MainViewDataProvideKey).getSelectRequestEnvironment();
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

        String httpMethod = requestParamManager.getHttpMethod().toString();
        //创建请求参数对象

        ControllerRequestData controllerRequestData = requestParamManager.getInvokeModelIndex() == 1 ?
                new DynamicControllerRequestData(httpMethod, url, controller.getId(),
                        beanInvokeSetting.isUseProxy(), beanInvokeSetting.isUseInterceptor(), false, ((DynamicController) controller)) :
                new ControllerRequestData(httpMethod, url, controller.getId(),
                        beanInvokeSetting.isUseProxy(), beanInvokeSetting.isUseInterceptor(), false);
        //设置请求参数
        requestParamManager.applyParam(controllerRequestData);
        //选择调用方式
        //保存缓存
        RequestCache requestCache = RequestCache.RequestCacheBuilder.aRequestCache()
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
        scriptSimpleLog.clearLog(controller.getId());
        if (javaCodeEngine.execRequest(new Request(controllerRequestData), requestCache.getRequestScript(), scriptSimpleLog)) {
            BasicControllerRequestCallMethod basicRequestCallMethod = getBaseRequest(controllerRequestData, controller);
            LOG.info(controllerRequestData.toString());
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

    private boolean runNewHttpRequestProgressTask(Controller controller, BasicControllerRequestCallMethod basicRequestCallMethod, CountDownLatch latch) {
        String invokeId = controller.getId();
        if (waitResponseThread.containsKey(invokeId)) {
            return false;
        }
        ProgressManager.getInstance().run(new Task.Backgroundable(project, "Send request") {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                try {
                    latch.await();
                } catch (InterruptedException ignored) {
                }
                Thread thread = Thread.currentThread();
                waitResponseThread.put(invokeId, thread);
                try {
                    Objects.requireNonNull(project.getUserData(Constant.RequestContextManagerKey)).put(invokeId, createRequestContext());
                    basicRequestCallMethod.invoke();
                    indicator.setText("Wait " + controller.getUrl() + " Response");
                    while (!indicator.isCanceled() && waitResponseThread.containsKey(invokeId)) {
                        LockSupport.parkNanos(thread, 500);
                    }
                    if (indicator.isCanceled())
                        cancelHttpRequest(controller.getId());
                } catch (Exception e) {
                    NotifyUtils.notification(project, e instanceof InvokeTimeoutException ? "Invoke Timeout" : "Invoke Fail，Cannot Connect");
                    cancelHttpRequest(controller.getId());
                }
            }
        });
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

    private BasicControllerRequestCallMethod getBaseRequest(ControllerRequestData controllerRequestData,
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
                new ReflexRequestCallMethod(((DynamicControllerRequestData) controllerRequestData), startPort, userProjectManager) :
                new HttpRequestCallMethod(controllerRequestData, simpleCallback);
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

    public boolean canEnabledSendButton(String id) {
        return buttonStateMap.getOrDefault(id, true);
    }
}
