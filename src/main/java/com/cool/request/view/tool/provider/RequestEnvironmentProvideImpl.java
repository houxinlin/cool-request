package com.cool.request.view.tool.provider;

import com.cool.request.common.bean.EmptyEnvironment;
import com.cool.request.common.bean.RequestEnvironment;
import com.cool.request.common.state.CoolRequestEnvironmentPersistentComponent;
import com.cool.request.utils.StringUtils;
import com.cool.request.view.main.RequestEnvironmentProvide;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

@Service
public final class RequestEnvironmentProvideImpl implements RequestEnvironmentProvide {
    private final Project project;

    public static RequestEnvironmentProvideImpl getInstance(Project project) {
        return project.getService(RequestEnvironmentProvideImpl.class);
    }

    public RequestEnvironmentProvideImpl(Project project) {
        this.project = project;
    }

    @Override
    public @NotNull RequestEnvironment getSelectRequestEnvironment() {
        CoolRequestEnvironmentPersistentComponent.State state = CoolRequestEnvironmentPersistentComponent.getInstance(project);
        if (StringUtils.isEmpty(state.getSelectId())) return new EmptyEnvironment();

        for (RequestEnvironment environment : state.getEnvironments()) {
            if (StringUtils.isEqualsIgnoreCase(state.getSelectId(), environment.getId())) return environment;
        }
        return new EmptyEnvironment();
    }


}
