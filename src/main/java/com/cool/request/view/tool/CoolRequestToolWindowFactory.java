/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * CoolRequestToolWindowFactory.java is part of Cool Request
 *
 * License: GPL-3.0+
 *
 * Cool Request is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Cool Request is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Cool Request.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.cool.request.view.tool;

import com.cool.request.common.constant.CoolRequestConfigConstant;
import com.cool.request.common.listener.component.cURLListener;
import com.cool.request.components.http.net.CommonOkHttpRequest;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.FocusWatcher;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.wm.WindowManager;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

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


        new cURLListener(project,toolWindow.getComponent()).install(toolWindow.getComponent());
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