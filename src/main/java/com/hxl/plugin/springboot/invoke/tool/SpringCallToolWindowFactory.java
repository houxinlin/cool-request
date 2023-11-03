package com.hxl.plugin.springboot.invoke.tool;

import com.hxl.plugin.springboot.invoke.view.CoolIdeaPluginWindowView;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import org.jetbrains.annotations.NotNull;

public class SpringCallToolWindowFactory implements ToolWindowFactory{
    public SpringCallToolWindowFactory() {
    }

    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        CoolIdeaPluginWindowView coolIdeaPluginWindowView = new CoolIdeaPluginWindowView(project);
        toolWindow.getContentManager().addContent(
                toolWindow.getContentManager().getFactory().createContent(coolIdeaPluginWindowView, "", true)
        );
    }

}