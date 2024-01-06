package com.hxl.plugin.springboot.invoke.action.actions;

import com.hxl.plugin.springboot.invoke.IdeaTopic;
import com.hxl.plugin.springboot.invoke.utils.ResourceBundleUtils;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class ChangeMainLayoutAnAction extends BaseAnAction {
    private final Project project;

    public ChangeMainLayoutAnAction(Project project) {
        super(project, () -> ResourceBundleUtils.getString("change.layout"), () -> ResourceBundleUtils.getString("change.layout"), AllIcons.Debugger.RestoreLayout);
        this.project = project;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        project.getMessageBus().syncPublisher(IdeaTopic.CHANGE_LAYOUT).event();

    }
}
