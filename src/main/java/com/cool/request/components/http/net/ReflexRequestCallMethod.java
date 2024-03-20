package com.cool.request.components.http.net;

import com.cool.request.components.http.HTTPResponseManager;
import com.cool.request.components.http.ReflexHttpRequestParamAdapterBody;
import com.cool.request.components.http.net.request.DynamicReflexHttpRequestParam;
import com.cool.request.components.http.net.request.HttpRequestParamUtils;
import com.cool.request.components.http.response.InvokeResponseModel;
import com.cool.request.lib.springmvc.BinaryBody;
import com.cool.request.lib.springmvc.Body;
import com.cool.request.lib.springmvc.EmptyBody;
import com.cool.request.lib.springmvc.FormBody;
import com.cool.request.rmi.starter.ICoolRequestStarterRMI;
import com.cool.request.utils.Base64Utils;
import com.cool.request.utils.UrlUtils;
import com.cool.request.view.main.HTTPEventListener;
import com.cool.request.view.tool.UserProjectManager;

import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class ReflexRequestCallMethod extends BasicReflexControllerRequestCallMethod {
    private final UserProjectManager userProjectManager;
    private final DynamicReflexHttpRequestParam reflexHttpRequestParam;

    public ReflexRequestCallMethod(DynamicReflexHttpRequestParam reflexHttpRequestParam,
                                   UserProjectManager userProjectManager) {
        super(reflexHttpRequestParam);
        this.reflexHttpRequestParam = reflexHttpRequestParam;

        this.userProjectManager = userProjectManager;
    }

    @Override
    public void invoke(RequestContext requestContext) {
//        ReflexRequestResponseListenerMap.getInstance(userProjectManager.getProject())
//                .register(((long) reflexHttpRequestParam.getAttachData()), requestContext);

//        ReflexControllerRequest reflexControllerRequest = new ReflexControllerRequest(port);
        ReflexHttpRequestParamAdapterBody reflexHttpRequestParamAdapter = ReflexHttpRequestParamAdapterBody
                .ReflexHttpRequestParamAdapterBuilder.aReflexHttpRequestParamAdapter()
                .withUrl(reflexHttpRequestParam.getUrl())
                .withContentType(HttpRequestParamUtils.getContentType(reflexHttpRequestParam, MediaTypes.TEXT))
                .withUseInterceptor(reflexHttpRequestParam.isUseInterceptor())
                .withUseProxyObject(reflexHttpRequestParam.isUseProxyObject())
                .withUserFilter(reflexHttpRequestParam.isUserFilter())
                .withHeaders(reflexHttpRequestParam.getHeaders())
                .withMethod(reflexHttpRequestParam.getMethod().toString())
                .build();

        reflexHttpRequestParamAdapter.setAttachData(reflexHttpRequestParam.getAttachData());
        reflexHttpRequestParamAdapter.setBody("");
        Body body = reflexHttpRequestParam.getBody();
        if (body != null && !(body instanceof EmptyBody)) {
            String contentType = HttpRequestParamUtils.getContentType(reflexHttpRequestParam, null);
            if (contentType == null) {
                reflexHttpRequestParamAdapter.setContentType(body.getMediaType());
            }
        }

        if (body instanceof FormBody) {
            reflexHttpRequestParamAdapter.setFormData(((FormBody) body).getData());
        } else if (body instanceof BinaryBody) {
            reflexHttpRequestParamAdapter.setBody(((BinaryBody) body).getSelectFile());
        } else {
            if (body != null && !(body instanceof EmptyBody)) {
                reflexHttpRequestParamAdapter.setBody(new String(body.contentConversion(), StandardCharsets.UTF_8));
            }
        }

        // 查找远程对象
        try {
            int port = userProjectManager.getRMIPortByProjectPort(UrlUtils.getPort(reflexHttpRequestParam.getUrl()));
            if (port != -1) {
                ICoolRequestStarterRMI coolRequestStarterRMI = (ICoolRequestStarterRMI) Naming
                        .lookup("rmi://localhost:" + port + "/" + ICoolRequestStarterRMI.class.getName());

                InvokeResponseModel invokeResponseModel = coolRequestStarterRMI.invokeController(reflexHttpRequestParamAdapter);
                HTTPResponseBody httpResponseBody = new HTTPResponseBody();
                httpResponseBody.setHeader(invokeResponseModel.getHeader());
                httpResponseBody.setCode(invokeResponseModel.getCode());
                byte[] responseBody = Base64Utils.decode(invokeResponseModel.getBaseBodyData());
                httpResponseBody.setSize(responseBody.length);
                responseBody = HTTPResponseManager.getInstance(userProjectManager.getProject())
                        .bodyConverter(responseBody, new HTTPHeader(httpResponseBody.getHeader()));
                if (responseBody != null) {
                    httpResponseBody.setBase64BodyData(Base64Utils.encodeToString(responseBody));
                }
                for (HTTPEventListener httpEventListener : requestContext.getHttpEventListeners()) {
                    httpEventListener.endSend(requestContext, httpResponseBody);
                }
                //通知全局的监听器
                HTTPResponseManager.getInstance(userProjectManager.getProject()).onHTTPResponse(httpResponseBody);
                return;
            }
            throw new IllegalArgumentException("Not Found RMI Port");
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        } catch (NotBoundException e) {
            throw new RuntimeException(e);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

}
