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
