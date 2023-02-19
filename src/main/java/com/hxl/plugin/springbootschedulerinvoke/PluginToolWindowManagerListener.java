package com.hxl.plugin.springbootschedulerinvoke;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.openapi.wm.ex.ToolWindowManagerListener;
import org.jetbrains.annotations.NotNull;

public class PluginToolWindowManagerListener implements ToolWindowManagerListener {
    private final Project project;

    public PluginToolWindowManagerListener(Project project) {
        this.project = project;
    }
    @Override
    public void stateChanged(@NotNull ToolWindowManager toolWindowManager) {
        ToolWindowManagerListener.super.stateChanged(toolWindowManager);
    }
}
