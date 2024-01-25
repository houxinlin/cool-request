package com.cool.request.action.actions;

import com.cool.request.icons.MyIcons;
import com.cool.request.utils.ResourceBundleUtils;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.openapi.wm.ToolWindowType;
import org.jetbrains.annotations.NotNull;

import static com.cool.request.Constant.PLUGIN_ID;

public class FloatWindowsAnAction extends BaseAnAction {
    public FloatWindowsAnAction(Project project) {
        super(project, () -> ResourceBundleUtils.getString("float.windows"),
                () -> ResourceBundleUtils.getString("float.windows"), MyIcons.WINDOW);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        assert project != null;
        ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow(PLUGIN_ID);
        if (toolWindow == null) {
            return;
        }
        if (!toolWindow.isActive()) {
            toolWindow.activate(null);
        }
        if (toolWindow.getType() == ToolWindowType.DOCKED) {
            toolWindow.setType(ToolWindowType.FLOATING, () -> {
            });
            return;
        }
        toolWindow.setType(ToolWindowType.DOCKED, () -> {
        });
    }
}
