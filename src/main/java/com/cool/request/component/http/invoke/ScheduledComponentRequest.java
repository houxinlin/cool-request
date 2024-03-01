package com.cool.request.component.http.invoke;

import com.cool.request.component.http.invoke.body.ReflexScheduledRequestBody;

public class ScheduledComponentRequest  extends BasicRemoteComponentRequest<ReflexScheduledRequestBody>{

    public ScheduledComponentRequest(int port) {
        super(port);
    }
}
