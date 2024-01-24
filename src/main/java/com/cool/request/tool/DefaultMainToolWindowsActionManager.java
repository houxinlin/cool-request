package com.cool.request.tool;

import com.cool.request.view.component.ApiToolPage;
import com.cool.request.view.component.MainBottomHTTPContainer;
import com.intellij.openapi.project.Project;
import icons.MyIcons;


public class DefaultMainToolWindowsActionManager extends MainToolWindowsActionManager {
    public DefaultMainToolWindowsActionManager(Project project) {
        super(project);
    }

    @Override
    protected void init() {
        super.init();
        registerAction(createMainToolWindowsAction(ApiToolPage.PAGE_NAME, MyIcons.SPRING, () -> new ApiToolPage(getProject(), false), false));
        registerAction(createMainToolWindowsAction(MainBottomHTTPContainer.PAGE_NAME, MyIcons.HTTP, () -> new MainBottomHTTPContainer(getProject()), false));
    }
}
