package com.cool.request.view.tool;

import com.cool.request.common.icons.CoolRequestIcons;
import com.cool.request.view.component.ApiToolPage;
import com.intellij.openapi.project.Project;

public class MergeApiAndRequestToolWindowsActionManager extends MainToolWindowsActionManager {
    public MergeApiAndRequestToolWindowsActionManager(Project project) {
        super(project);
    }

    @Override
    protected void init() {
        super.init();
        registerAction(createMainToolWindowsAction(ApiToolPage.PAGE_NAME, CoolRequestIcons.SPRING, () -> ApiToolPage.getInstance(getProject()), false));
    }
}
