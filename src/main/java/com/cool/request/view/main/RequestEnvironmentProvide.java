package com.cool.request.view.main;

import com.cool.request.common.bean.RequestEnvironment;
import com.cool.request.view.tool.Provider;
import org.jetbrains.annotations.NotNull;

public interface RequestEnvironmentProvide extends Provider {
    @NotNull
    public RequestEnvironment getSelectRequestEnvironment();
}

