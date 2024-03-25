package com.cool.request.view.tool;

import com.cool.request.common.constant.CoolRequestConfigConstant;
import com.cool.request.common.listener.component.cURLListener;
import com.cool.request.components.http.net.CommonOkHttpRequest;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.wm.WindowManager;
import org.jetbrains.annotations.NotNull;

/**
 * 右侧 Tool window 工厂类
 */
public class CoolRequestToolWindowFactory extends CommonOkHttpRequest implements ToolWindowFactory, DumbAware {
    public CoolRequestToolWindowFactory() {
    }

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        toolWindow.getContentManager().addContent(
                toolWindow.getContentManager().getFactory().createContent(new MainToolWindows(project), "", false)
        );
        toolWindow.setShowStripeButton(true);
        toolWindow.setSplitMode(false, () -> {
        });
        project.putUserData(CoolRequestConfigConstant.ToolWindowKey, toolWindow);
        WindowManager.getInstance().getFrame(project).addWindowFocusListener(new cURLListener(project));
//        WindowManager.getInstance().getFrame(project).addWindowFocusListener(new WindowAdapter() {
//            @Override
//            public void windowGainedFocus(WindowEvent e) {
//                super.windowGainedFocus(e);
//                if (!project.isDisposed())return;
//                project.getMessageBus().syncPublisher(CoolRequestIdeaTopic.IDEA_FRAME_EVENT_TOPIC).windowGainedFocus(e);
//            }
//
//            @Override
//            public void windowLostFocus(WindowEvent e) {
//                super.windowLostFocus(e);
//                if (!project.isDisposed())return;
//                project.getMessageBus().syncPublisher(CoolRequestIdeaTopic.IDEA_FRAME_EVENT_TOPIC).windowLostFocus(e);
//            }
//        });
//        WindowManager.getInstance().getFrame(project).addComponentListener(new ComponentAdapter() {
//            @Override
//            public void componentResized(ComponentEvent e) {
//                if (!project.isDisposed())return;
//                project.getMessageBus().syncPublisher(CoolRequestIdeaTopic.IDEA_FRAME_EVENT_TOPIC).windowsResizedEvent(e);
//            }
//
//            @Override
//            public void componentMoved(ComponentEvent e) {
//                if (!project.isDisposed())return;
//                project.getMessageBus().syncPublisher(CoolRequestIdeaTopic.IDEA_FRAME_EVENT_TOPIC).windowsMovedEvent(e);
//            }
//        });
    }

}