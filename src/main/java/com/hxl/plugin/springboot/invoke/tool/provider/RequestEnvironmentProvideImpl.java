package com.hxl.plugin.springboot.invoke.tool.provider;

import com.hxl.plugin.springboot.invoke.bean.EmptyEnvironment;
import com.hxl.plugin.springboot.invoke.bean.RequestEnvironment;
import com.hxl.plugin.springboot.invoke.bean.components.controller.Controller;
import com.hxl.plugin.springboot.invoke.state.CoolRequestEnvironmentPersistentComponent;
import com.hxl.plugin.springboot.invoke.utils.StringUtils;
import com.hxl.plugin.springboot.invoke.view.main.RequestEnvironmentProvide;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class RequestEnvironmentProvideImpl implements RequestEnvironmentProvide {
    private Project project;

    public RequestEnvironmentProvideImpl(Project project) {
        this.project = project;
    }

    @Override
    public @NotNull RequestEnvironment getSelectRequestEnvironment() {
        CoolRequestEnvironmentPersistentComponent.State state = project.getService(CoolRequestEnvironmentPersistentComponent.class).getState();
        if (StringUtils.isEmpty(state.getSelectId())) return new EmptyEnvironment();

        for (RequestEnvironment environment : state.getEnvironments()) {
            if (StringUtils.isEqualsIgnoreCase(state.getSelectId(), environment.getId())) return environment;
        }
        return new EmptyEnvironment();
    }

    @Override
    public String applyUrl(Controller controller) {
        if (getSelectRequestEnvironment() instanceof EmptyEnvironment) {
            return StringUtils.joinUrlPath("http://localhost:" + controller.getServerPort(),
                    StringUtils.getFullUrl(controller));
        }
        return StringUtils.joinUrlPath(getSelectRequestEnvironment().getHostAddress(), StringUtils.getFullUrl(controller));
    }
}
