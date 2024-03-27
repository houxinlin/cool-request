/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * ApifoxColoredTreeCellRenderer.java is part of Cool Request
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

package com.cool.request.plugin.apifox;

import com.intellij.icons.AllIcons;
import com.intellij.ui.ColoredTreeCellRenderer;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class ApifoxColoredTreeCellRenderer  extends ColoredTreeCellRenderer {
    @Override
    public void customizeCellRenderer(@NotNull JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        if (value instanceof ApifoxProjectFolderSelectDialog.TeamTreeNode) {
            ApifoxProjectFolderSelectDialog.TeamTreeNode node = (ApifoxProjectFolderSelectDialog.TeamTreeNode) value;
            append(node.getTeam().getName());
            setIcon(AllIcons.CodeWithMe.CwmAccess);
        }else if (value instanceof ApifoxProjectFolderSelectDialog.ProjectTreeNode){
            ApifoxProjectFolderSelectDialog.ProjectTreeNode node = (ApifoxProjectFolderSelectDialog.ProjectTreeNode) value;
            setIcon(AllIcons.Actions.ProjectDirectory);
            append(node.getProject().getName());
        }else if (value instanceof ApifoxProjectFolderSelectDialog.FolderTreeNode){
            ApifoxProjectFolderSelectDialog.FolderTreeNode node = (ApifoxProjectFolderSelectDialog.FolderTreeNode) value;
            appendTextPadding(10);
            setIcon(AllIcons.Nodes.Folder);
            append(node.getFolder().getName());
        }
    }
}
