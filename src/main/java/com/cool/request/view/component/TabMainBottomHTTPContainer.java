package com.cool.request.view.component;

import com.cool.request.common.bean.components.controller.Controller;
import com.intellij.openapi.project.Project;

public class TabMainBottomHTTPContainer extends MainBottomHTTPContainer {
    public TabMainBottomHTTPContainer(Project project, Controller controller) {
        super(project, controller);
    }

    public TabMainBottomHTTPContainer(Project project) {
        super(project);
    }
}
