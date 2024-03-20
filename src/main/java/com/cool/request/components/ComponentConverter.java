package com.cool.request.components;

import com.cool.request.common.bean.components.Component;
import com.intellij.openapi.project.Project;

public interface ComponentConverter<S extends Component, T extends Component> {
    public boolean canSupport(Component source, Component target);

    public T converter(Project project,Component s, Component target);
}
