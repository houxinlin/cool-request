package com.cool.request.component.http.net.response;

import com.cool.request.common.constant.CoolRequestIdeaTopic;
import com.cool.request.common.model.ErrorHTTPResponseBody;
import com.cool.request.component.http.HTTPResponseListener;
import com.cool.request.component.http.HTTPResponseManager;
import com.cool.request.component.http.net.HTTPHeader;
import com.cool.request.component.http.net.HTTPResponseBody;
import com.cool.request.component.http.net.Header;
import com.cool.request.component.http.net.HttpRequestCallMethod;
import com.cool.request.utils.Base64Utils;
import com.intellij.openapi.project.Project;
import okhttp3.Headers;
import okhttp3.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HTTPCallMethodResponse implements HttpRequestCallMethod.SimpleCallback {
    private final Project project;
    private final Map<String, Thread> waitResponseThread;
    private final List<HTTPResponseListener> httpResponseListeners;

    public HTTPCallMethodResponse(Project project,
                                  Map<String, Thread> waitResponseThread,
                                  List<HTTPResponseListener> httpResponseListeners) {
        this.project = project;
        this.waitResponseThread = waitResponseThread;
        this.httpResponseListeners = httpResponseListeners;
    }

    @Override
    public void onResponse(String requestId, int code, Response response) {
        if (!waitResponseThread.containsKey(requestId)) {
            return;
        }

        Headers okHttpHeaders = response.headers();
        List<Header> headers = new ArrayList<>();
        int headerCount = okHttpHeaders.size();
        for (int i = 0; i < headerCount; i++) {
            String headerName = okHttpHeaders.name(i);
            String headerValue = okHttpHeaders.value(i);
            headers.add(new Header(headerName, headerValue));
        }
        HTTPResponseBody httpResponseBody = new HTTPResponseBody();

        httpResponseBody.setBase64BodyData("");
        httpResponseBody.setCode(response.code());
        httpResponseBody.setId(requestId);
        httpResponseBody.setHeader(headers);
        if (response.body() != null) {
            try {
                byte[] bytes = response.body().bytes();
                bytes = HTTPResponseManager.getInstance(project).bodyConverter(bytes, new HTTPHeader(headers));
                httpResponseBody.setBase64BodyData(Base64Utils.encodeToString(bytes));
            } catch (IOException ignored) {
            }
        }
        for (HTTPResponseListener httpResponseListener : httpResponseListeners) {
            httpResponseListener.onResponseEvent(requestId, httpResponseBody);
        }
        //通知全局的监听器
        HTTPResponseManager.getInstance(project).onHTTPResponse(httpResponseBody);
    }

    @Override
    public void onError(String requestId, IOException e) {
        ErrorHTTPResponseBody errorHTTPResponseBody = new ErrorHTTPResponseBody(e.getMessage().getBytes());
        for (HTTPResponseListener httpResponseListener : httpResponseListeners) {
            httpResponseListener.onResponseEvent(requestId, errorHTTPResponseBody);
        }

        project.getMessageBus()
                .syncPublisher(CoolRequestIdeaTopic.HTTP_RESPONSE)
                .onResponseEvent(requestId, errorHTTPResponseBody);
    }
}
