/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * CustomControllerFolderTreeCellRenderer.java is part of Cool Request
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

package com.cool.request.view.dialog;

import com.cool.request.common.state.CustomControllerFolderPersistent;
import com.intellij.icons.AllIcons;
import com.intellij.ui.ColoredTreeCellRenderer;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class CustomControllerFolderTreeCellRenderer extends ColoredTreeCellRenderer {
    @Override
    public void customizeCellRenderer(@NotNull JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        if (value instanceof CustomControllerFolderSelectDialog.FolderTreeNode) {
            CustomControllerFolderSelectDialog.FolderTreeNode folderTreeNode = (CustomControllerFolderSelectDialog.FolderTreeNode) value;
            Object userObject = folderTreeNode.getUserObject();
            if (userObject instanceof CustomControllerFolderPersistent.Folder) {
                append(((CustomControllerFolderPersistent.Folder) userObject).getName());
            }
            setIcon(AllIcons.Nodes.Folder);
        }
    }
}
