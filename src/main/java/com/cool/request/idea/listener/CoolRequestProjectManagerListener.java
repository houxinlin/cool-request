package com.cool.request.idea.listener;

import com.cool.request.common.listener.component.cURLListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManagerListener;
import com.intellij.openapi.wm.WindowManager;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.List;

public class CoolRequestProjectManagerListener implements ProjectManagerListener {
    @Override
    public void projectOpened(@NotNull Project project) {
        ProjectManagerListener.super.projectOpened(project);
    }

    @Override
    public void projectClosed(@NotNull Project project) {
        ProjectManagerListener.super.projectClosed(project);
        JFrame frame = WindowManager.getInstance().getFrame(project);
        if (frame != null) {
            List<WindowListener> result = new ArrayList<>();
            for (WindowListener windowListener : frame.getWindowListeners()) {
                if (windowListener instanceof cURLListener) {
                    result.add(windowListener);
                }
            }
            for (WindowListener windowListener : result) {
                frame.removeWindowListener(windowListener);
            }
        }
    }
}
