/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * RequestManager.java is part of Cool Request
 *
 * License: GPL-3.0+
 *
 * Cool Request is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Cool Request is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Cool Request.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.cool.request.components.http.net;

import com.cool.request.common.bean.BeanInvokeSetting;
import com.cool.request.common.bean.EmptyEnvironment;
import com.cool.request.common.bean.RequestEnvironment;
import com.cool.request.common.cache.ComponentCacheManager;
import com.cool.request.common.exception.RequestParamException;
import com.cool.request.components.http.*;
import com.cool.request.components.http.invoke.InvokeTimeoutException;
import com.cool.request.components.http.net.request.DynamicReflexHttpRequestParam;
import com.cool.request.components.http.net.request.HttpRequestParamUtils;
import com.cool.request.components.http.net.request.StandardHttpRequestParam;
import com.cool.request.components.http.net.request.UserAgentHTTPRequestParamApply;
import com.cool.request.components.http.net.response.HTTPCallMethodResponse;
import com.cool.request.components.http.script.*;
import com.cool.request.lib.springmvc.EmptyBody;
import com.cool.request.lib.springmvc.RequestCache;
import com.cool.request.utils.MessagesWrapperUtils;
import com.cool.request.utils.NotifyUtils;
import com.cool.request.utils.ResourceBundleUtils;
import com.cool.request.utils.StringUtils;
import com.cool.request.utils.exception.UserCancelRequestException;
import com.cool.request.utils.param.HTTPParameterProvider;
import com.cool.request.utils.param.PanelParameterProvider;
import com.cool.request.utils.url.UriComponentsBuilder;
import com.cool.request.view.main.HTTPEventListener;
import com.cool.request.view.main.HTTPEventOrder;
import com.cool.request.view.main.IRequestParamManager;
import com.cool.request.view.tool.Provider;
import com.cool.request.view.tool.UserProjectManager;
import com.cool.request.view.tool.provider.RequestEnvironmentProvideImpl;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.LockSupport;
import java.util.function.Consumer;

import static com.cool.request.view.main.HTTPEventOrder.MAX;

public class RequestManager implements Provider, Disposable {
    private static final Logger LOG = Logger.getInstance(RequestManager.class);
    private final IRequestParamManager requestParamManager;
    private final Project project;
    private final UserProjectManager userProjectManager;
    private final Map<RequestContext, Thread> waitResponseThread = new ConcurrentHashMap<>();
    private final Map<Class<? extends Exception>, Consumer<Exception>> exceptionHandler = new HashMap<>();
    private final Consumer<Exception> defaultExceptionHandler;
    private final List<String> activeHttpRequestIds = new ArrayList<>();
    private final List<HTTPResponseListener> httpResponseListeners = new ArrayList<>();
    private final List<HTTPRequestParamApply> httpRequestParamApplies = new ArrayList<>();

    public RequestManager(IRequestParamManager requestParamManager,
                          Project project,
                          UserProjectManager userProjectManager) {
        this.requestParamManager = requestParamManager;
        this.project = project;
        this.userProjectManager = userProjectManager;
        defaultExceptionHandler = e -> NotifyUtils.notification(project, "Request Fail:" + e.getMessage());
        exceptionHandler.put(InvokeTimeoutException.class, e -> NotifyUtils.notification(project, "Invoke Timeout"));
        exceptionHandler.put(RequestParamException.class, e -> MessagesWrapperUtils.showErrorDialog(e.getMessage(),
                ResourceBundleUtils.getString("tip")));

        httpRequestParamApplies.add(new UserAgentHTTPRequestParamApply());
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
                url = StringUtils.joinUrlPath("http://localhost:" + controller.getServerPort(), controller.getContextPath(), controller.getUrl());
            }
        }
        return url;
    }

    /**
     * 发送请求
     *
     * @return 如果发送成功则返回true，异步
     */
    public boolean sendRequest(RequestContext requestContext) {
        //如果没有选择节点，则停止
        Controller controller = requestContext.getController();
        if (!preRequest(controller)) return false;

        //任务线程还没有开启时，用户再次点击了发送，这种情况几率非常小
        //当waitResponseThread中添加了任务时候，activeHttpRequestIds会被移除
        if (activeHttpRequestIds.contains(controller.getId())) {
            return false;
        }
        try {
            //需要确保开启子线程发送请求时后，waitResponseThread在下次点击时候必须存在，防止重复
            if (waitResponseThread.containsKey(requestContext)) {
                MessagesWrapperUtils.showErrorDialog(ResourceBundleUtils.getString("wait.previous.end"), ResourceBundleUtils.getString("tip"));
                return false;
            }
            RequestEnvironment selectRequestEnvironment = RequestEnvironmentProvideImpl.getInstance(project).getSelectRequestEnvironment();

            //使用用户输入的url和method
            String url = generatorRequestURL(selectRequestEnvironment, controller);
            //创建请求参数对象
            StandardHttpRequestParam standardHttpRequestParam = createStandardHttpRequestParam(controller);
            standardHttpRequestParam.setId(controller.getId());
            //应用参数从参数面板和全局变量
            HTTPParameterProvider panelParameterProvider = new PanelParameterProvider(requestParamManager);

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
                ComponentCacheManager.storageRequestCache(controller.getId(), requestCache);
            }
            //参数apply
            for (HTTPRequestParamApply httpRequestParamApply : httpRequestParamApplies) {
                httpRequestParamApply.apply(project, standardHttpRequestParam);
            }
            installScriptExecute(requestContext);
            activeHttpRequestIds.add(controller.getId());
            ProgressManager.getInstance().run(new HTTPRequestTaskBackgroundable(project, requestContext, standardHttpRequestParam));
            return true;
        } catch (Exception e) {
            activeHttpRequestIds.remove(controller.getId());
        }
        return false;
    }

    private StandardHttpRequestParam createStandardHttpRequestParam(Controller controller) {
        BeanInvokeSetting beanInvokeSetting = requestParamManager.getBeanInvokeSetting();
        if (controller instanceof CustomController) return new StandardHttpRequestParam();

        long l = System.currentTimeMillis();

        return requestParamManager.isReflexRequest() ?
                new DynamicReflexHttpRequestParam(beanInvokeSetting.isUseProxy(), beanInvokeSetting.isUseInterceptor(), false, l) :
                new StandardHttpRequestParam();
    }

    private void installScriptExecute(RequestContext requestContext) {
        SimpleScriptLog simpleScriptLog = new SimpleScriptLog(requestContext, requestParamManager);
        requestContext.setScriptExecute(new SimpleScriptExecute(requestParamManager.getRequestScript(),
                requestParamManager.getResponseScript(), simpleScriptLog));
    }

    class HTTPRequestTaskBackgroundable extends Task.Backgroundable {
        private final Project project;
        private final RequestContext requestContext;
        private final StandardHttpRequestParam standardHttpRequestParam;

        public HTTPRequestTaskBackgroundable(Project project, RequestContext requestContext,
                                             StandardHttpRequestParam standardHttpRequestParam) {
            super(project, "Request init...");
            this.project = project;
            this.requestContext = requestContext;
            this.standardHttpRequestParam = standardHttpRequestParam;
        }

        @Override
        public void run(@NotNull ProgressIndicator indicator) {
            try {
                requestContext.getHttpEventListeners().add(new ClearStatusHTTPListener());
                requestParamManager.beginSend(requestContext, indicator);

                //创建请求上下文，请求执行阶段可能会产生额外数据，都通过createRequestContext来中转
                SimpleScriptLog simpleScriptLog = new SimpleScriptLog(requestContext, requestParamManager);
                requestParamManager.getScriptLogPage().clearAllLog();
                boolean canRequest = false;
                try {
                    indicator.setText("Execute pre script");
                    indicator.setFraction(0.8);
                    canRequest = requestContext.getScriptExecute()
                            .execRequest(project, new Request(standardHttpRequestParam, simpleScriptLog));
                } catch (Exception e) {
                    //脚本编写不对可能出现异常，请求也同时停止
                    e.printStackTrace(new ErrorScriptLog(simpleScriptLog));
                    MessagesWrapperUtils.showErrorDialog(e.getMessage(),
                            e instanceof CompilationException ?
                                    "Request Script Syntax Error ,Please Check!" : "Request Script Run Error");
                    //脚本出现异常后停止
                    throw e;
                }
                if (indicator.isCanceled()) throw new UserCancelRequestException();//脚本执行的时候可能被取消
                //脚本没拦截本次请求，用户返回了true
                if (canRequest) {
                    BasicControllerRequestCallMethod basicRequestCallMethod = getControllerRequestCallMethod(standardHttpRequestParam, requestContext);

                    requestContext.getHttpEventListeners().add(new ResponseScriptExec(project));
                    //发送请求，上一个相同请求可能被发起，则停止
                    requestContext.beginSend(indicator);

                    //在开始HTTPEventListener监听下可能被取消
                    if (indicator.isCanceled()) throw new UserCancelRequestException();
                    indicator.setFraction(0.9);
                    if (!runHttpRequestTask(requestContext, basicRequestCallMethod, indicator)) {
                        MessagesWrapperUtils.showErrorDialog("Unable to execute, waiting for the previous task to end", ResourceBundleUtils.getString("tip"));
                    }
                } else {
                    MessagesWrapperUtils.showInfoMessage(ResourceBundleUtils.getString("http.request.rejected"), ResourceBundleUtils.getString("tip"));
                    httpExceptionTermination(requestContext);
                }
            } catch (Exception e) {
                httpExceptionTermination(requestContext);
                if (!(e instanceof UserCancelRequestException)) {
                    exceptionHandler.getOrDefault(e.getClass(), defaultExceptionHandler).accept(e);
                }
            }
        }
    }

    private boolean runHttpRequestTask(RequestContext requestContext,
                                       BasicControllerRequestCallMethod basicRequestCallMethod,
                                       @NotNull ProgressIndicator indicator) throws Exception {
        if (waitResponseThread.containsKey(requestContext)) {
            return false;
        }
        Thread thread = Thread.currentThread();
        waitResponseThread.put(requestContext, thread);
        activeHttpRequestIds.remove(requestContext.getId());
        HttpRequestTaskExecute httpRequestTask = new HttpRequestTaskExecute(requestContext, basicRequestCallMethod);
        httpRequestTask.run(indicator);
        return true;
    }


    /**
     * http请求异常被终止
     */
    public void httpExceptionTermination(RequestContext requestContext) {
        String requestId = requestContext.getController().getId();
        activeHttpRequestIds.remove(requestId);
        Thread thread = waitResponseThread.get(requestContext);
        if (thread != null) {
            LockSupport.unpark(thread);
            waitResponseThread.remove(requestContext);
        }
        requestContext.endSend(null);

    }

    @HTTPEventOrder(MAX + 1)
    private class ClearStatusHTTPListener implements HTTPEventListener {
        @Override
        public void endSend(RequestContext requestContext, HTTPResponseBody httpResponseBody, ProgressIndicator progressIndicator) {
            String requestId = requestContext.getController().getId();
            activeHttpRequestIds.remove(requestId);
            waitResponseThread.remove(requestContext);
            requestParamManager.endSend(requestContext, httpResponseBody, progressIndicator);
        }
    }

    private static class ResponseScriptExec implements HTTPEventListener {
        private final Project project;

        public ResponseScriptExec(Project project) {
            this.project = project;
        }

        @Override
        public void endSend(RequestContext requestContext, HTTPResponseBody httpResponseBody, ProgressIndicator progressIndicator) {
            if (httpResponseBody != null) {
                progressIndicator.setText("Exec post script");
                requestContext.getScriptExecute().execResponse(project, requestContext, new Response(httpResponseBody));
            }
        }
    }

    private static class ErrorScriptLog extends PrintStream {
        private final SimpleScriptLog scriptSimpleLog;

        public ErrorScriptLog(SimpleScriptLog scriptSimpleLog) {
            super(new ByteArrayOutputStream());
            this.scriptSimpleLog = scriptSimpleLog;
        }

        @Override
        public void print(String value) {
            scriptSimpleLog.println(value);
        }
    }


    private BasicControllerRequestCallMethod getControllerRequestCallMethod(StandardHttpRequestParam standardHttpRequestParam,
                                                                            RequestContext requestContext) {

        HttpRequestCallMethod.SimpleCallback simpleCallback =
                new HTTPCallMethodResponse(project, waitResponseThread, httpResponseListeners, requestContext);
        if (requestContext.getController() instanceof CustomController) {
            return new HttpRequestCallMethod(standardHttpRequestParam, simpleCallback);
        }
        return requestParamManager.isReflexRequest() ?
                new ReflexRequestCallMethod(((DynamicReflexHttpRequestParam) standardHttpRequestParam),
                        waitResponseThread,
                        userProjectManager) :
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
        private final RequestContext requestContext;

        public HttpRequestTaskExecute(RequestContext requestContext,
                                      BasicControllerRequestCallMethod basicRequestCallMethod) {
            this.basicControllerRequestCallMethod = basicRequestCallMethod;
            this.requestContext = requestContext;
        }

        public void run(@NotNull ProgressIndicator indicator) throws Exception {
            Thread thread = Thread.currentThread();
            indicator.setText("Wait " + requestContext.getController().getUrl() + " response");
            basicControllerRequestCallMethod.invoke(requestContext);
            while (!indicator.isCanceled() && waitResponseThread.containsKey(requestContext)) {
                LockSupport.parkNanos(thread, 500);
            }
            if (indicator.isCanceled()) {
                httpExceptionTermination(requestContext);
            }
        }
    }

    public boolean canEnabledSendButton(String id) {
        for (RequestContext requestContext : waitResponseThread.keySet()) {
            if (StringUtils.isEqualsIgnoreCase(requestContext.getController().getId(), id)) return false;
        }
        return true;
    }

    @Override
    public void dispose() {
        removeAllData();
    }
}
