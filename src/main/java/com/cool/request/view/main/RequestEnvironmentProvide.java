package com.cool.request.view.main;

import com.cool.request.common.bean.RequestEnvironment;
import com.cool.request.common.bean.components.controller.Controller;
import com.cool.request.component.http.net.request.StandardHttpRequestParam;
import com.cool.request.view.tool.Provider;
import org.jetbrains.annotations.NotNull;

public interface RequestEnvironmentProvide extends Provider {
    @NotNull
    public RequestEnvironment getSelectRequestEnvironment();

    public String applyUrl(Controller controller);

    public void applyEnvironmentParam(StandardHttpRequestParam standardHttpRequestParam);

}

