package com.hxl.plugin.springboot.invoke.tool;

import com.hxl.plugin.springboot.invoke.Constant;
import com.hxl.plugin.springboot.invoke.IdeaTopic;
import com.hxl.plugin.springboot.invoke.net.CommonOkHttpRequest;
import com.hxl.plugin.springboot.invoke.net.VersionInfoReport;
import com.hxl.plugin.springboot.invoke.utils.NavigationUtils;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.wm.WindowManager;
import org.jetbrains.annotations.NotNull;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;

/**
 * 右侧 Tool window 工厂类
 */
public class SpringCallToolWindowFactory extends CommonOkHttpRequest implements ToolWindowFactory, DumbAware {

    private static final VersionInfoReport versionReport = new VersionInfoReport();

    public SpringCallToolWindowFactory() {
    }

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        try {
            versionReport.report();
        } catch (Exception ignored) {
        }

        CoolRequest.initCoolRequest(project);
        toolWindow.getContentManager().addContent(
                toolWindow.getContentManager().getFactory().createContent(new MainToolWindows(project), "", false)
        );
        toolWindow.setShowStripeButton(true);
        toolWindow.setSplitMode(false, () -> {
        });
        WindowManager.getInstance().getFrame(project).addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                project.getMessageBus().syncPublisher(IdeaTopic.IDEA_FRAME_EVENT_TOPIC).windowsResizedEvent(e);
            }

            @Override
            public void componentMoved(ComponentEvent e) {
                project.getMessageBus().syncPublisher(IdeaTopic.IDEA_FRAME_EVENT_TOPIC).windowsMovedEvent(e);
            }
        });
    }

}