package com.cool.request.action.actions;

import com.cool.request.common.icons.CoolRequestIcons;
import com.cool.request.view.main.IRequestParamManager;
import com.cool.request.view.tool.ProviderManager;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class SaveCustomControllerAnAction extends BaseAnAction {
    public SaveCustomControllerAnAction(Project project) {
        super(project, () -> "Save", CoolRequestIcons.SAVE);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        ProviderManager.findAndConsumerProvider(IRequestParamManager.class, Objects.requireNonNull(e.getProject()),
                IRequestParamManager::saveAsCustomController);

    }
}
