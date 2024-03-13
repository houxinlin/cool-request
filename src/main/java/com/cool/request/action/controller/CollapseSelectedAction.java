package com.cool.request.action.controller;

import com.cool.request.action.actions.DynamicAnAction;
import com.cool.request.common.icons.KotlinCoolRequestIcons;
import com.cool.request.utils.ResourceBundleUtils;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.util.ui.tree.TreeUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.util.List;

public class CollapseSelectedAction extends DynamicAnAction {
    private JTree tree;

    public CollapseSelectedAction(JTree tree, Project project) {
        super(project, () -> ResourceBundleUtils.getString("collapse"), KotlinCoolRequestIcons.INSTANCE.getCOLLAPSE());
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
