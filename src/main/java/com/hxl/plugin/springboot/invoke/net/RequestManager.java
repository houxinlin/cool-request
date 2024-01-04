package com.hxl.plugin.springboot.invoke.net;

import com.hxl.plugin.springboot.invoke.Constant;
import com.hxl.plugin.springboot.invoke.IdeaTopic;
import com.hxl.plugin.springboot.invoke.bean.BeanInvokeSetting;
import com.hxl.plugin.springboot.invoke.invoke.ControllerInvoke;
import com.hxl.plugin.springboot.invoke.invoke.InvokeTimeoutException;
import com.hxl.plugin.springboot.invoke.model.ErrorInvokeResponseModel;
import com.hxl.plugin.springboot.invoke.model.InvokeResponseModel;
import com.hxl.plugin.springboot.invoke.model.RequestMappingModel;
import com.hxl.plugin.springboot.invoke.script.JavaCodeEngine;
import com.hxl.plugin.springboot.invoke.script.Request;
import com.hxl.plugin.springboot.invoke.script.ScriptSimpleLogImpl;
import com.hxl.plugin.springboot.invoke.springmvc.RequestCache;
import com.hxl.plugin.springboot.invoke.utils.NotifyUtils;
import com.hxl.plugin.springboot.invoke.utils.RequestParamCacheManager;
import com.hxl.plugin.springboot.invoke.utils.UserProjectManager;
import com.hxl.plugin.springboot.invoke.view.IRequestParamManager;
import com.hxl.plugin.springboot.invoke.view.page.ScriptPage;
import com.intellij.idea.LoggerFactory;
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
    private final ScriptSimpleLogImpl scriptSimpleLog ;

    public RequestManager(IRequestParamManager requestParamManager, Project project, UserProjectManager userProjectManager) {
        this.requestParamManager = requestParamManager;
        this.project = project;
        this.userProjectManager = userProjectManager;
        this.scriptSimpleLog =new ScriptSimpleLogImpl(project);
    }

    private RequestContext createRequestContext(){
        return  new RequestContext();
    }
    public void sendRequest(RequestMappingModel requestMappingModel) {
        if (requestMappingModel == null) {
            NotifyUtils.notification(project,"Please Select a Node");
            return;
        }
        //使用用户输入的url和method
        String url = requestParamManager.getUrl();
        BeanInvokeSetting beanInvokeSetting = requestParamManager.getBeanInvokeSetting();

        int port = requestMappingModel.getPort();
        String httpMethod = requestParamManager.getHttpMethod().toString();
        //创建请求参数对象
        ControllerInvoke.ControllerRequestData controllerRequestData =
                new ControllerInvoke.ControllerRequestData(httpMethod, url, requestMappingModel.getController().getId(),
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
                .withPort(port)
                .withScriptLog("")
                .withRequestScript(requestParamManager.getRequestScript())
                .withResponseScript(requestParamManager.getResponseScript())
                .withContentPath(requestMappingModel.getContextPath())
                .withUseProxy(beanInvokeSetting.isUseProxy())
                .withUseInterceptor(beanInvokeSetting.isUseInterceptor())
                .withInvokeModelIndex(requestParamManager.getInvokeModelIndex())
                .build();
        RequestParamCacheManager.setCache(requestMappingModel.getController().getId(), requestCache);

        if (!checkUrl(url)) {
            NotifyUtils.notification(project, "Invalid URL");
            return;
        }
        JavaCodeEngine javaCodeEngine = new JavaCodeEngine();
        scriptSimpleLog.clearLog(requestMappingModel.getController().getId());
        if (javaCodeEngine.execRequest(new Request(controllerRequestData), requestCache.getRequestScript(),scriptSimpleLog)) {
            BasicRequestCallMethod basicRequestCallMethod = getBaseRequest(controllerRequestData, port);
            LOG.info(controllerRequestData.toString());
            CountDownLatch countDownLatch = new CountDownLatch(1);
            if (!runNewHttpRequestProgressTask(requestMappingModel, basicRequestCallMethod,countDownLatch)) {
                Messages.showErrorDialog("Unable to execute, waiting for the previous task to end", "Tip");
            } else {
                //有数据不安全行为，只能等这里执行完在让子任务运行
                requestParamManager.setSendButtonEnabled(false);
            }
            countDownLatch.countDown();
        }
    }

    public boolean runNewHttpRequestProgressTask(RequestMappingModel requestMappingModel, BasicRequestCallMethod basicRequestCallMethod, CountDownLatch latch) {
        String invokeId = requestMappingModel.getController().getId();
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
                    Objects.requireNonNull(project.getUserData(Constant.RequestContextManagerKey)).put(invokeId,createRequestContext());
                    basicRequestCallMethod.invoke();
                    indicator.setText("Wait " + requestMappingModel.getController().getUrl() + " Response");
                    while (!indicator.isCanceled() && waitResponseThread.containsKey(invokeId)) {
                        LockSupport.parkNanos(thread, 500);
                    }
                    if (indicator.isCanceled())
                        cancelHttpRequest(requestMappingModel.getController().getId());
                } catch (Exception e) {
                    NotifyUtils.notification(project,e instanceof InvokeTimeoutException ? "Invoke Timeout" : "Invoke Fail，Cannot Connect");
                    cancelHttpRequest(requestMappingModel.getController().getId());
                }
            }
        });
        buttonStateMap.put(requestMappingModel.getController().getId(), false);

        return true;
    }

    public void cancelHttpRequest(String requestId) {
        Thread thread = waitResponseThread.get(requestId);
        if (thread != null) {
            LockSupport.unpark(thread);
            waitResponseThread.remove(requestId);
        }
        buttonStateMap.remove(requestId);
        if (requestId.equalsIgnoreCase(requestParamManager.getCurrentRequestMappingModel().getController().getId())) {
            requestParamManager.setSendButtonEnabled(true);
        }
    }

    private BasicRequestCallMethod getBaseRequest(ControllerInvoke.ControllerRequestData controllerRequestData, int port) {
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
        return requestParamManager.getInvokeModelIndex() == 1 ?
                new ReflexRequestCallMethod(controllerRequestData, port, userProjectManager) :
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
