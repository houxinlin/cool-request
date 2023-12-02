package com.hxl.plugin.springboot.invoke.action.ui;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.openapi.wm.ToolWindowType;
import org.jetbrains.annotations.NotNull;

import static com.hxl.plugin.springboot.invoke.Constant.PLUGIN_ID;

public class FloatWindowsAnAction  extends AnAction {
    public FloatWindowsAnAction() {
        super("Float Windows", "Float windows",AllIcons.Actions.MoveToWindow);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        assert project != null;
        ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow(PLUGIN_ID);
        if (toolWindow==null) return;
        if (!toolWindow.isActive()) {
            toolWindow.activate(null);
        }
        if (toolWindow.getType() ==ToolWindowType.DOCKED){
            toolWindow.setType(ToolWindowType.WINDOWED, () -> {});
            return;
        }
        toolWindow.setType(ToolWindowType.DOCKED, () -> {});
    }
}
