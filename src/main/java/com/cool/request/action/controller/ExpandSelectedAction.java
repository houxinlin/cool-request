/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * ExpandSelectedAction.java is part of Cool Request
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

package com.cool.request.action.controller;

import com.cool.request.action.actions.DynamicAnAction;
import com.cool.request.common.icons.CoolRequestIcons;
import com.cool.request.common.icons.KotlinCoolRequestIcons;
import com.cool.request.utils.ResourceBundleUtils;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.util.ui.tree.TreeUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.util.Enumeration;
import java.util.List;

public class ExpandSelectedAction extends DynamicAnAction {
    private JTree tree;

    public ExpandSelectedAction(JTree tree, Project project) {
        super(project,()->ResourceBundleUtils.getString("expand"), KotlinCoolRequestIcons.INSTANCE.getEXPANDALL());
        this.tree = tree;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        List<TreePath> treePaths = TreeUtil.collectSelectedPaths(tree);
        for (TreePath treePath : treePaths) {
            expandPath(treePath);

        }
    }

    private void expandPath(TreePath treePath) {
        TreeNode node = (TreeNode) treePath.getLastPathComponent();
        if (node.getChildCount() >= 0) {
            for (Enumeration<? extends TreeNode> children = node.children(); children.hasMoreElements(); ) {
                TreeNode n = (TreeNode) children.nextElement();
                TreePath path = treePath.pathByAddingChild(n);
                expandPath(path);
            }
        }
        tree.expandPath(treePath);
    }

}
