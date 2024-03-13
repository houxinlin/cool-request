package com.cool.request.component.http.net;

import com.cool.request.common.model.ReflexHTTPResponseBody;
import com.cool.request.component.http.HTTPResponseManager;
import com.cool.request.component.http.invoke.body.ReflexHttpRequestParamAdapterBody;
import com.cool.request.component.http.net.mina.MessageCallback;
import com.cool.request.component.http.net.request.DynamicReflexHttpRequestParam;
import com.cool.request.component.http.net.request.HttpRequestParamUtils;
import com.cool.request.component.http.reflex.ReflexRequest;
import com.cool.request.lib.springmvc.BinaryBody;
import com.cool.request.lib.springmvc.Body;
import com.cool.request.lib.springmvc.EmptyBody;
import com.cool.request.lib.springmvc.FormBody;
import com.cool.request.utils.Base64Utils;
import com.cool.request.utils.GsonUtils;
import com.cool.request.view.main.HTTPEventListener;
import com.cool.request.view.tool.UserProjectManager;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;

import java.nio.charset.StandardCharsets;

public class ReflexRequestCallMethod extends BasicReflexControllerRequestCallMethod {
    private final int port;
    private final UserProjectManager userProjectManager;
    private final DynamicReflexHttpRequestParam reflexHttpRequestParam;
    private ReflexRequest reflexRequest;

    public ReflexRequestCallMethod(DynamicReflexHttpRequestParam reflexHttpRequestParam,
                                   int port,
                                   UserProjectManager userProjectManager) {
        super(reflexHttpRequestParam);
        this.reflexHttpRequestParam = reflexHttpRequestParam;
        this.port = port;
        this.userProjectManager = userProjectManager;
    }

    @Override
    public void invoke(RequestContext requestContext) {
        reflexRequest = new ReflexRequest(port, 1000 * 30, new ResponseMessageCallback(requestContext));
//        ReflexRequestResponseListenerMap.getInstance(userProjectManager.getProject())
//                .register(((long) reflexHttpRequestParam.getAttachData()), requestContext);

//        ReflexControllerRequest reflexControllerRequest = new ReflexControllerRequest(port);
        ReflexHttpRequestParamAdapterBody reflexHttpRequestParamAdapter = ReflexHttpRequestParamAdapterBody
                .ReflexHttpRequestParamAdapterBuilder.aReflexHttpRequestParamAdapter()
                .withId(reflexHttpRequestParam.getId())
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
        reflexHttpRequestParamAdapter.setAttachData(reflexHttpRequestParam.getAttachData());

        reflexRequest.connection();
        reflexRequest.request(GsonUtils.toJsonString(reflexHttpRequestParamAdapter));
    }

    class ResponseMessageCallback implements MessageCallback {
        private final RequestContext requestContext;

        public ResponseMessageCallback(RequestContext requestContext) {
            this.requestContext = requestContext;
        }

        @Override
        public void messageReceived(IoSession session, Object message) {
            IoBuffer ioBuffer = (IoBuffer) message;
            byte[] array = ioBuffer.array();
            String msg = new String(array, StandardCharsets.UTF_8);

            ReflexHTTPResponseBody httpResponseBody = GsonUtils.readValue(msg, ReflexHTTPResponseBody.class);
            if (httpResponseBody == null) return;
            httpResponseBody.setId(userProjectManager.getDynamicControllerRawId(httpResponseBody.getId()));

            byte[] responseBody = Base64Utils.decode(httpResponseBody.getBase64BodyData());
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
            if (reflexRequest != null) {
                reflexRequest.close();
            }

        }
    }
}
