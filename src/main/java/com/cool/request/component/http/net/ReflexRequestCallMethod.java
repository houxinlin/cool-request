package com.cool.request.component.http.net;

import com.cool.request.component.http.invoke.InvokeException;
import com.cool.request.component.http.invoke.InvokeResult;
import com.cool.request.component.http.invoke.InvokeTimeoutException;
import com.cool.request.component.http.invoke.ReflexControllerRequest;
import com.cool.request.component.http.net.request.DynamicReflexHttpRequestParam;
import com.cool.request.component.http.net.request.HttpRequestParamUtils;
import com.cool.request.component.http.net.request.ReflexHttpRequestParam;
import com.cool.request.component.http.net.request.ReflexHttpRequestParamAdapter;
import com.cool.request.lib.springmvc.BinaryBody;
import com.cool.request.lib.springmvc.Body;
import com.cool.request.lib.springmvc.EmptyBody;
import com.cool.request.lib.springmvc.FormBody;
import com.cool.request.view.tool.UserProjectManager;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ReflexRequestCallMethod extends BasicControllerRequestCallMethod {
    private final int port;
    private final UserProjectManager userProjectManager;

    public ReflexRequestCallMethod(DynamicReflexHttpRequestParam controllerRequestData,
                                   int port,
                                   UserProjectManager userProjectManager) {
        super(controllerRequestData);
        this.port = port;
        this.userProjectManager = userProjectManager;
    }

    @Override
    public void invoke() throws InvokeException {
        CountDownLatch countDownLatch = new CountDownLatch(1);

        ReflexHttpRequestParam reflexHttpRequestParam = ((ReflexHttpRequestParam) getInvokeData());
        ReflexControllerRequest reflexControllerRequest = new ReflexControllerRequest(port);
        ReflexHttpRequestParamAdapter reflexHttpRequestParamAdapter = ReflexHttpRequestParamAdapter
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
        Body body = reflexHttpRequestParam.getBody();
        if (body instanceof FormBody) {
            reflexHttpRequestParamAdapter.setFormData(((FormBody) body).getData());
        } else if (body instanceof BinaryBody) {
            reflexHttpRequestParamAdapter.setBody(((BinaryBody) body).getSelectFile());
        } else {
            if (body != null && !(body instanceof EmptyBody)) {
                reflexHttpRequestParamAdapter.setBody(new String(body.contentConversion()));
            }
        }

        if (reflexControllerRequest.requestSync(reflexHttpRequestParamAdapter) == InvokeResult.FAIL) {
            throw new InvokeException();
        }
        userProjectManager.registerWaitReceive(getInvokeData().getId(), countDownLatch);
        try {
            if (!countDownLatch.await(1, TimeUnit.SECONDS)) {
                throw new InvokeTimeoutException();
            }
        } catch (InterruptedException e) {
            throw new InvokeTimeoutException();
        }
    }
}
