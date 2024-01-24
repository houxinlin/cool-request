package com.cool.request.tool;

import com.cool.request.view.component.ApiToolPage;
import com.intellij.openapi.project.Project;
import icons.MyIcons;

public class MergeApiAndRequestToolWindowsActionManager extends MainToolWindowsActionManager {
    public MergeApiAndRequestToolWindowsActionManager(Project project) {
        super(project);
    }

    @Override
    protected void init() {
        super.init();
        registerAction(createMainToolWindowsAction(ApiToolPage.PAGE_NAME, MyIcons.SPRING, () -> new ApiToolPage(getProject(), true), false));
    }
}
