/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * TreePathUtils.java is part of Cool Request
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

package com.cool.request.utils;

import javax.swing.*;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.util.Enumeration;

public class TreePathUtils {
    public static boolean is(TreePath treePath, Class<?> targetClass) {
        if (treePath == null) return false;
        Object lastPathComponent = treePath.getLastPathComponent();
        if (lastPathComponent == null) return false;
        return targetClass.isInstance(lastPathComponent);
    }

    public static <T> T getNode(TreePath treePath, Class<T> targetClass) {
        Object lastPathComponent = treePath.getLastPathComponent();
        if (targetClass.isInstance(lastPathComponent)) {
            return ((T) lastPathComponent);
        }
        return null;
    }

    public static void expandAll(JTree tree) {
        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.expandRow(i);
        }
    }

    public static void expandPath(JTree tree, TreePath treePath) {
        TreeNode node = (TreeNode) treePath.getLastPathComponent();
        if (node.getChildCount() >= 0) {
            for (Enumeration<? extends TreeNode> children = node.children(); children.hasMoreElements(); ) {
                TreeNode n = children.nextElement();
                TreePath path = treePath.pathByAddingChild(n);
                expandPath(tree, path);
            }
        }
        tree.expandPath(treePath);
    }
}
