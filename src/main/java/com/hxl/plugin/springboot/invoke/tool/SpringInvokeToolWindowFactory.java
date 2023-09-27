package com.hxl.plugin.springboot.invoke.tool;

import com.hxl.plugin.springboot.invoke.view.PluginWindowToolBarView;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import org.jetbrains.annotations.NotNull;

public class SpringInvokeToolWindowFactory implements ToolWindowFactory{
    public SpringInvokeToolWindowFactory() {
    }

    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        PluginWindowToolBarView pluginWindowView = new PluginWindowToolBarView(project);
        toolWindow.getContentManager().addContent(
                toolWindow.getContentManager().getFactory().createContent(pluginWindowView, "", true)
        );
    }

}