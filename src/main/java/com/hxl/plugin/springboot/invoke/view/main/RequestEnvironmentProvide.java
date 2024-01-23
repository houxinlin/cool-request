package com.hxl.plugin.springboot.invoke.view.main;

import com.hxl.plugin.springboot.invoke.bean.RequestEnvironment;
import com.hxl.plugin.springboot.invoke.bean.components.controller.Controller;
import com.hxl.plugin.springboot.invoke.tool.Provider;
import org.jetbrains.annotations.NotNull;

public interface RequestEnvironmentProvide extends Provider {
    @NotNull
    public RequestEnvironment getSelectRequestEnvironment();

    public String applyUrl(Controller controller);

}
