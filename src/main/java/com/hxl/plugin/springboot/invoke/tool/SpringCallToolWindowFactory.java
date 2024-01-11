package com.hxl.plugin.springboot.invoke.tool;

import com.hxl.plugin.springboot.invoke.net.CommonOkHttpRequest;
import com.hxl.plugin.springboot.invoke.net.VersionInfoReport;
import com.hxl.plugin.springboot.invoke.view.CoolIdeaPluginWindowView;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import org.jetbrains.annotations.NotNull;

public class SpringCallToolWindowFactory extends CommonOkHttpRequest implements ToolWindowFactory {

    private static final VersionInfoReport versionReport = new VersionInfoReport();
    public SpringCallToolWindowFactory() {
    }

    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        try {
            versionReport.report();
        } catch (Exception ignored) {
        }
//        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
//            postBody("http://plugin.houxinlin.com/api/exception", throwable.getMessage(), "text/paint", null).enqueue(new EmptyCallback());
//        });
        CoolRequest coolRequest = CoolRequest.initCoolRequest(project);

        CoolIdeaPluginWindowView coolIdeaPluginWindowView = new CoolIdeaPluginWindowView(project);
        coolRequest.attachWindowView(coolIdeaPluginWindowView);

        toolWindow.getContentManager().addContent(
                toolWindow.getContentManager().getFactory().createContent(coolIdeaPluginWindowView, "", false)
        );
    }

}