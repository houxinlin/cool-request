package com.cool.request.component.http.invoke;

import com.cool.request.component.http.invoke.body.PullDynamicRequestBody;

public class ReflexPullDynamicRequest extends BasicRemoteComponentRequest<PullDynamicRequestBody> {
    public ReflexPullDynamicRequest(int port) {
        super(port);
    }
}
