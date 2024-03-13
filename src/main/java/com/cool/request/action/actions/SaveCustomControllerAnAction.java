package com.cool.request.action.actions;

import com.cool.request.common.icons.KotlinCoolRequestIcons;
import com.cool.request.view.component.MainBottomHTTPContainer;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class SaveCustomControllerAnAction extends DynamicAnAction {
    private final MainBottomHTTPContainer mainBottomHTTPContainer;

    public SaveCustomControllerAnAction(Project project, MainBottomHTTPContainer mainBottomHTTPContainer) {
        super(project, () -> "Save", KotlinCoolRequestIcons.INSTANCE.getSAVE());
        this.mainBottomHTTPContainer = mainBottomHTTPContainer;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        mainBottomHTTPContainer.getMainBottomHttpInvokeViewPanel()
                .getHttpRequestParamPanel()
                .saveAsCustomController();
    }
}
