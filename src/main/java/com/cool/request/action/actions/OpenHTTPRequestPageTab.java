/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * OpenHTTPRequestPageTab.java is part of Cool Request
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

package com.cool.request.action.actions;

import com.cool.request.components.http.Controller;
import com.cool.request.utils.ResourceBundleUtils;
import com.cool.request.utils.TreePathUtils;
import com.cool.request.view.editor.CoolHTTPRequestVirtualFile;
import com.cool.request.view.main.MainTopTreeView;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.ui.tree.TreeUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.TreePath;

public class OpenHTTPRequestPageTab extends BaseAnAction {
    private MainTopTreeView mainTopTreeView;

    public OpenHTTPRequestPageTab(Project project, MainTopTreeView mainTopTreeView, Icon icon) {
        super(project, () -> ResourceBundleUtils.getString("open.http.request.new.tab"), icon);
        this.mainTopTreeView = mainTopTreeView;
    }

    private Controller getController() {
        TreePath selectedPathIfOne = TreeUtil.getSelectedPathIfOne(mainTopTreeView.getTree());
        if (selectedPathIfOne != null) {
            MainTopTreeView.RequestMappingNode requestMappingNode = TreePathUtils.getNode(selectedPathIfOne, MainTopTreeView.RequestMappingNode.class);
            assert requestMappingNode != null;
            return requestMappingNode.getData();
        }
        return null;
    }

    private VirtualFile createNewFile() {
        Controller controller = getController();
        if (controller != null) {
            return new CoolHTTPRequestVirtualFile(controller);
        }
        return null;

    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project != null) {
            ApplicationManager.getApplication().runWriteAction(() -> {
                VirtualFile virtualFile = createNewFile();
                if (virtualFile != null) {
                    FileEditorManager.getInstance(project).openFile(virtualFile, true);
                }
            });
        }
    }
}
