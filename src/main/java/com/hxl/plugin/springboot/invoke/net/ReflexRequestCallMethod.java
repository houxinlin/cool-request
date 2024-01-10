package com.hxl.plugin.springboot.invoke.net;

import com.hxl.plugin.springboot.invoke.cool.request.DynamicControllerRequestData;
import com.hxl.plugin.springboot.invoke.cool.request.ReflexControllerRequest;
import com.hxl.plugin.springboot.invoke.invoke.InvokeException;
import com.hxl.plugin.springboot.invoke.invoke.InvokeResult;
import com.hxl.plugin.springboot.invoke.invoke.InvokeTimeoutException;
import com.hxl.plugin.springboot.invoke.net.request.ControllerRequestData;
import com.hxl.plugin.springboot.invoke.utils.UserProjectManager;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ReflexRequestCallMethod extends BasicControllerRequestCallMethod {
    private final int port;
    private final UserProjectManager userProjectManager;

    public ReflexRequestCallMethod(DynamicControllerRequestData controllerRequestData,
                                   int port,
                                   UserProjectManager userProjectManager) {
        super(controllerRequestData);
        this.port = port;
        this.userProjectManager = userProjectManager;
    }

    @Override
    public void invoke() throws InvokeException {
        CountDownLatch countDownLatch = new CountDownLatch(1);

        ReflexControllerRequest reflexControllerRequest = new ReflexControllerRequest(port);
        if (reflexControllerRequest.requestSync(getInvokeData()) == InvokeResult.FAIL) {
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
