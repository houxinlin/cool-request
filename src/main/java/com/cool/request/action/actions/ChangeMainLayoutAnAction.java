package com.cool.request.action.actions;

import com.cool.request.IdeaTopic;
import com.cool.request.utils.ResourceBundleUtils;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class ChangeMainLayoutAnAction extends BaseAnAction {
    private final Project project;

    /**
     * change layout.
     *
     * @param project The project in which the action is being created.
     */
    public ChangeMainLayoutAnAction(Project project) {
        super(project, () -> ResourceBundleUtils.getString("change.layout"), () -> ResourceBundleUtils.getString("change.layout"), AllIcons.Debugger.RestoreLayout);
        this.project = project;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        project.getMessageBus().syncPublisher(IdeaTopic.CHANGE_LAYOUT).event();

    }
}
