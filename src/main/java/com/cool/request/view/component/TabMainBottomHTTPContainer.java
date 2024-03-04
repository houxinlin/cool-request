package com.cool.request.view.component;

import com.cool.request.common.bean.components.controller.Controller;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;

public class TabMainBottomHTTPContainer extends MainBottomHTTPContainer {
    public TabMainBottomHTTPContainer(Project project, Controller controller, Disposable disposable) {
        super(project, controller, disposable);
    }

    public TabMainBottomHTTPContainer(Project project, Disposable disposable) {
        super(project, disposable);
    }
}
