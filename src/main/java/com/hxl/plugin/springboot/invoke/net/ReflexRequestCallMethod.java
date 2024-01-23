package com.hxl.plugin.springboot.invoke.net;

import com.hxl.plugin.springboot.invoke.cool.request.DynamicReflexHttpRequestParam;
import com.hxl.plugin.springboot.invoke.cool.request.ReflexControllerRequest;
import com.hxl.plugin.springboot.invoke.invoke.InvokeException;
import com.hxl.plugin.springboot.invoke.invoke.InvokeResult;
import com.hxl.plugin.springboot.invoke.invoke.InvokeTimeoutException;
import com.hxl.plugin.springboot.invoke.net.request.*;
import com.hxl.plugin.springboot.invoke.springmvc.BinaryBody;
import com.hxl.plugin.springboot.invoke.springmvc.Body;
import com.hxl.plugin.springboot.invoke.springmvc.FormBody;
import com.hxl.plugin.springboot.invoke.utils.UserProjectManager;

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
            if (body != null) {
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
