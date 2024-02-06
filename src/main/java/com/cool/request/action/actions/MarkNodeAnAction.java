package com.cool.request.action.actions;

import com.cool.request.common.icons.CoolRequestIcons;
import com.cool.request.common.state.MarkPersistent;
import com.cool.request.view.main.MainTopTreeView;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.util.ui.tree.TreeUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.util.List;

public class MarkNodeAnAction extends BaseAnAction {
    private JTree jTree;

    public MarkNodeAnAction(Project project, JTree jTree) {
        super(project, () -> "Mark", CoolRequestIcons.MARK);
        this.jTree = jTree;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        List<TreePath> treePaths = TreeUtil.collectSelectedPaths(this.jTree);
        for (TreePath treePath : treePaths) {
            Object lastPathComponent = treePath.getLastPathComponent();
            if (lastPathComponent instanceof MainTopTreeView.RequestMappingNode) {
                MarkPersistent.getInstance(getProject()).getState().getControllerMark()
                        .add(((MainTopTreeView.RequestMappingNode) lastPathComponent).getData().getId());
            }
            if (lastPathComponent instanceof MainTopTreeView.ScheduledMethodNode) {
                MarkPersistent.getInstance(getProject()).getState().getScheduleMark()
                        .add(((MainTopTreeView.ScheduledMethodNode) lastPathComponent).getData().getId());
            }
        }
    }
}
