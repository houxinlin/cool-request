package com.cool.request.action.actions;

import com.cool.request.common.icons.CoolRequestIcons;
import com.cool.request.common.state.MarkPersistent;
import com.cool.request.utils.ResourceBundleUtils;
import com.cool.request.view.main.MainTopTreeView;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.util.ui.tree.TreeUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.tree.TreePath;
import java.util.List;

public class UnMarkAnAction extends BaseAnAction {
    private final MainTopTreeView mainTopTreeView;

    public UnMarkAnAction(Project project, MainTopTreeView mainTopTreeView) {
        super(project, () -> ResourceBundleUtils.getString("unmark"), CoolRequestIcons.MARK);
        this.mainTopTreeView = mainTopTreeView;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        List<TreePath> treePaths = TreeUtil.collectSelectedPaths(this.mainTopTreeView.getTree());
        for (TreePath treePath : treePaths) {
            Object lastPathComponent = treePath.getLastPathComponent();
            if (lastPathComponent instanceof MainTopTreeView.RequestMappingNode) {
                MarkPersistent.getInstance(getProject()).getState().getControllerMark()
                        .remove(((MainTopTreeView.RequestMappingNode) lastPathComponent).getData().getId());
            }
            if (lastPathComponent instanceof MainTopTreeView.ScheduledMethodNode) {
                MarkPersistent.getInstance(getProject()).getState().getScheduleMark()
                        .remove(((MainTopTreeView.ScheduledMethodNode) lastPathComponent).getData().getId());
            }
        }
        if (mainTopTreeView.getApiToolPage().isMarkSelected()) {
            TreeUtil.removeSelected(mainTopTreeView.getTree());
        }

    }
}
