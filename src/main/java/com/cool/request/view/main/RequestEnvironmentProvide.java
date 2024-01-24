package com.cool.request.view.main;

import com.cool.request.bean.RequestEnvironment;
import com.cool.request.bean.components.controller.Controller;
import com.cool.request.net.request.StandardHttpRequestParam;
import com.cool.request.tool.Provider;
import org.jetbrains.annotations.NotNull;

public interface RequestEnvironmentProvide extends Provider {
    @NotNull
    public RequestEnvironment getSelectRequestEnvironment();

    public String applyUrl(Controller controller);

    public void applyEnvironmentParam(StandardHttpRequestParam standardHttpRequestParam);

}

