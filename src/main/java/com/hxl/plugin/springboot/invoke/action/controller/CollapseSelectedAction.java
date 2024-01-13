package com.hxl.plugin.springboot.invoke.action.controller;

import com.hxl.plugin.springboot.invoke.utils.ResourceBundleUtils;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.util.ui.tree.TreeUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.util.Enumeration;
import java.util.List;

public class CollapseSelectedAction extends AnAction {
    private JTree tree;

    public CollapseSelectedAction(JTree tree) {
        getTemplatePresentation().setText(ResourceBundleUtils.getString("collapse"));
        getTemplatePresentation().setIcon(AllIcons.Actions.Collapseall);
        this.tree = tree;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        List<TreePath> treePaths = TreeUtil.collectSelectedPaths(tree);
        for (TreePath treePath : treePaths) {
            collapsePath(treePath);
        }
    }

    private void collapsePath(TreePath treePath) {
        tree.collapsePath(treePath);
    }
}
