package com.cool.request.view.tool;

import com.cool.request.common.constant.CoolRequestIdeaTopic;
import com.cool.request.common.listener.component.cURLListener;
import com.cool.request.component.http.net.CommonOkHttpRequest;
import com.cool.request.component.http.net.VersionInfoReport;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.wm.WindowManager;
import org.jetbrains.annotations.NotNull;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

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

       CoolRequest.initCoolRequest(project).installProviders();
        toolWindow.getContentManager().addContent(
                toolWindow.getContentManager().getFactory().createContent(new MainToolWindows(project), "", false)
        );
        toolWindow.setShowStripeButton(true);
        toolWindow.setSplitMode(false, () -> {
        });
        WindowManager.getInstance().getFrame(project).addWindowFocusListener(new cURLListener(project));
        WindowManager.getInstance().getFrame(project).addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                super.windowGainedFocus(e);
                project.getMessageBus().syncPublisher(CoolRequestIdeaTopic.IDEA_FRAME_EVENT_TOPIC).windowGainedFocus(e);
            }

            @Override
            public void windowLostFocus(WindowEvent e) {
                super.windowLostFocus(e);
                project.getMessageBus().syncPublisher(CoolRequestIdeaTopic.IDEA_FRAME_EVENT_TOPIC).windowLostFocus(e);
            }
        });
        WindowManager.getInstance().getFrame(project).addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                project.getMessageBus().syncPublisher(CoolRequestIdeaTopic.IDEA_FRAME_EVENT_TOPIC).windowsResizedEvent(e);
            }

            @Override
            public void componentMoved(ComponentEvent e) {
                project.getMessageBus().syncPublisher(CoolRequestIdeaTopic.IDEA_FRAME_EVENT_TOPIC).windowsMovedEvent(e);
            }
        });
    }

}