/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * CoolRequestProjectManagerListener.java is part of Cool Request
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
