package com.hxl.plugin.springbootschedulerinvoke;


import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;


public class MyToolWindowFactory implements ToolWindowFactory {

    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        InvokeToolWindow invokeToolWindow = new InvokeToolWindow(toolWindow, project);
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(invokeToolWindow.getContent(), "", false);
        toolWindow.getContentManager().addContent(content);
    }

}