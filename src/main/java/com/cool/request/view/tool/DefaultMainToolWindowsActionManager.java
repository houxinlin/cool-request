package com.cool.request.view.tool;

import com.cool.request.common.icons.CoolRequestIcons;
import com.cool.request.common.icons.KotlinCoolRequestIcons;
import com.cool.request.common.service.ProjectViewSingleton;
import com.cool.request.view.component.ApiToolPage;
import com.cool.request.view.component.MainBottomHTTPContainer;
import com.intellij.openapi.project.Project;


public class DefaultMainToolWindowsActionManager extends MainToolWindowsActionManager {
    public DefaultMainToolWindowsActionManager(Project project) {
        super(project);
    }

    @Override
    protected void init() {
        super.init();
        registerAction(createMainToolWindowsAction(ApiToolPage.PAGE_NAME, KotlinCoolRequestIcons.INSTANCE.getMAIN(), () ->  ApiToolPage.getInstance(getProject()), false));
        registerAction(createMainToolWindowsAction(MainBottomHTTPContainer.PAGE_NAME, KotlinCoolRequestIcons.INSTANCE.getMAIN(), () -> ProjectViewSingleton.getInstance(getProject())
                .createAndGetMainBottomHTTPContainer(), false));
    }
}
