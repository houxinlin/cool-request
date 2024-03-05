package com.cool.request.component.http;

import com.cool.request.common.constant.CoolRequestIdeaTopic;
import com.cool.request.component.http.net.HTTPHeader;
import com.cool.request.component.http.net.HTTPResponseBody;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;

import java.util.ArrayList;
import java.util.List;

@Service
public final class HTTPResponseManager {
    private Project project;
    private List<HTTPResponseBodyConverter> httpResponseBodyFactories = new ArrayList<>();

    public HTTPResponseManager() {
        httpResponseBodyFactories.add(new OverflowHttpResponseBodyConverter());
    }

    public static HTTPResponseManager getInstance(Project project) {
        HTTPResponseManager service = project.getService(HTTPResponseManager.class);
        service.project = project;
        return service;
    }

    public byte[] bodyConverter(byte[] body, HTTPHeader header) {
        byte[] newHTTPResponseBody = body;
        for (HTTPResponseBodyConverter httpResponseBodyConverter : httpResponseBodyFactories) {
            newHTTPResponseBody = httpResponseBodyConverter.bodyConverter(newHTTPResponseBody, header);
        }
        return newHTTPResponseBody;
    }

    public void onHTTPResponse(HTTPResponseBody httpResponseBody) {

        project.getMessageBus().syncPublisher(CoolRequestIdeaTopic.HTTP_RESPONSE)
                .onResponseEvent(httpResponseBody.getId(), httpResponseBody, null);
    }
}
