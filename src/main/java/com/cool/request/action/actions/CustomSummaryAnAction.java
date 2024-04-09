/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * CustomSummaryAnAction.java is part of Cool Request
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
import com.cool.request.components.http.CustomController;
import com.cool.request.utils.ResourceBundleUtils;
import com.cool.request.utils.TreePathUtils;
import com.cool.request.view.main.MainTopTreeView;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.util.ui.tree.TreeUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.TreePath;

public class CustomSummaryAnAction extends BaseAnAction {
    private final MainTopTreeView mainTopTreeView;

    public CustomSummaryAnAction(Project project, MainTopTreeView mainTopTreeView, Icon icon) {
        super(project, () -> ResourceBundleUtils.getString("setting.summary"), icon);
        this.mainTopTreeView = mainTopTreeView;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        TreePath treePath = TreeUtil.getSelectedPathIfOne(mainTopTreeView.getTree());
        if (treePath == null) return;
        MainTopTreeView.RequestMappingNode requestMappingNode = TreePathUtils.getNode(treePath, MainTopTreeView.RequestMappingNode.class);
        if (requestMappingNode == null) return;
        Controller controller = requestMappingNode.getData();

        if (controller instanceof CustomController) {
            String summary = Messages.showInputDialog("", "Summary", null, ((CustomController) controller).getSummary(), null);
            if (summary != null) {
                ((CustomController) controller).setSummary(summary);
            }
        }
    }
}
