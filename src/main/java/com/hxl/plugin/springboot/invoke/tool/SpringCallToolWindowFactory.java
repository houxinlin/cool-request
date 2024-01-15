package com.hxl.plugin.springboot.invoke.tool;

import com.hxl.plugin.springboot.invoke.net.CommonOkHttpRequest;
import com.hxl.plugin.springboot.invoke.net.VersionInfoReport;
import com.hxl.plugin.springboot.invoke.view.CoolIdeaPluginWindowView;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import org.jetbrains.annotations.NotNull;

/**
 * 右侧 Tool window 工厂类
 */
public class SpringCallToolWindowFactory extends CommonOkHttpRequest implements ToolWindowFactory {

    private static final VersionInfoReport versionReport = new VersionInfoReport();
    public SpringCallToolWindowFactory() {
    }

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        try {
            versionReport.report();
        } catch (Exception ignored) {
        }
//        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
//            postBody("https://plugin.houxinlin.com/api/exception", throwable.getMessage(), "text/paint", null).enqueue(new EmptyCallback());
//        });
        CoolRequest coolRequest = CoolRequest.initCoolRequest(project);

        CoolIdeaPluginWindowView coolIdeaPluginWindowView = new CoolIdeaPluginWindowView(project);

/*
        // tool window header add action
       if (toolWindow instanceof ToolWindowEx) {
            DefaultActionGroup group = new DefaultActionGroup();
            ((ToolWindowEx) toolWindow).setAnchor(ToolWindowAnchor.RIGHT, null);
            group.add(new CollapseAction(), Constraints.LAST);
            group.add(new ExpandAction(), Constraints.LAST);
            group.addSeparator();
            ((ToolWindowEx) toolWindow).setTabActions(group);
        }*/
        coolRequest.attachWindowView(coolIdeaPluginWindowView);
        coolIdeaPluginWindowView.attachProject(project);
        toolWindow.getContentManager().addContent(
                toolWindow.getContentManager().getFactory().createContent(coolIdeaPluginWindowView, "", false)
        );
    }

}