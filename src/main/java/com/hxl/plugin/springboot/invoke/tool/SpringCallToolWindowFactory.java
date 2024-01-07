package com.hxl.plugin.springboot.invoke.tool;

import com.hxl.plugin.springboot.invoke.net.CommonOkHttpRequest;
import com.hxl.plugin.springboot.invoke.net.EmptyCallback;
import com.hxl.plugin.springboot.invoke.net.VersionInfoReport;
import com.hxl.plugin.springboot.invoke.view.CoolIdeaPluginWindowView;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import org.jetbrains.annotations.NotNull;

public class SpringCallToolWindowFactory extends CommonOkHttpRequest implements ToolWindowFactory {
    public SpringCallToolWindowFactory() {
    }

    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        try {
            new VersionInfoReport().report();
        } catch (Exception ignored) {
        }
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            postBody("http://plugin.houxinlin.com/api/exception", throwable.getMessage(), "text/paint", null).enqueue(new EmptyCallback());
        });

        CoolIdeaPluginWindowView coolIdeaPluginWindowView = new CoolIdeaPluginWindowView(project);
        toolWindow.getContentManager().addContent(
                toolWindow.getContentManager().getFactory().createContent(coolIdeaPluginWindowView, "", false)
        );
    }

}