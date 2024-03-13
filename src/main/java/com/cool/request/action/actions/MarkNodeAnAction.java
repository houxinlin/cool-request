package com.cool.request.action.actions;

import com.cool.request.common.bean.components.Component;
import com.cool.request.common.icons.KotlinCoolRequestIcons;
import com.cool.request.common.state.MarkPersistent;
import com.cool.request.utils.ResourceBundleUtils;
import com.cool.request.view.main.MainTopTreeView;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.util.ui.tree.TreeUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.util.HashSet;
import java.util.List;

public class MarkNodeAnAction extends DynamicAnAction {
    private final JTree jTree;

    public MarkNodeAnAction(Project project, JTree jTree) {
        super(project, () -> ResourceBundleUtils.getString("mark"), KotlinCoolRequestIcons.INSTANCE.getMARK());
        this.jTree = jTree;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        List<TreePath> treePaths = TreeUtil.collectSelectedPaths(this.jTree);
        for (TreePath treePath : treePaths) {
            Object lastPathComponent = treePath.getLastPathComponent();
            if (lastPathComponent instanceof MainTopTreeView.TreeNode) {
                Object component = ((MainTopTreeView.TreeNode<?>) lastPathComponent).getData();
                MarkPersistent.getInstance(getProject())
                        .getState()
                        .getMarkComponentMap().computeIfAbsent(((Component) component).getComponentType(), componentType -> new HashSet<>())
                        .add(((Component) component).getId());
            }
        }
    }
}
