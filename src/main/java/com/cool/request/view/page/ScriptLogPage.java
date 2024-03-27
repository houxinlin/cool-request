/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * ScriptLogPage.java is part of Cool Request
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

package com.cool.request.view.page;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.PlainTextFileType;
import com.intellij.openapi.project.Project;
import com.intellij.ui.tabs.TabInfo;
import com.intellij.ui.tabs.impl.JBTabsImpl;

import javax.swing.*;
import java.awt.*;

public class ScriptLogPage extends JPanel {
    private final Log logPage;

    public ScriptLogPage(Project project) {
        this.setLayout(new BorderLayout());
        JBTabsImpl jbTabs = new JBTabsImpl(project);
        this.logPage = new Log(project);
        jbTabs.addTab(new TabInfo(logPage).setText("Log"));
        add(jbTabs.getComponent(), BorderLayout.CENTER);
    }

    public void setLog(String value) {
        try {
            SwingUtilities.invokeLater(() -> logPage.setText(value));
        } catch (Exception ignored) {
        }
    }

    public void clearAllLog() {
        try {
            SwingUtilities.invokeLater(() -> logPage.setText(""));
        } catch (Exception ignored) {

        }
    }

    static class Log extends BasicEditPage {
        public Log(Project project) {
            super(project);
        }

        @Override
        public FileType getFileType() {
            return PlainTextFileType.INSTANCE;
        }
    }

}
