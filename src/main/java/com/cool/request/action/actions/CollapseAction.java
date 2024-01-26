package com.cool.request.action.actions;

import com.cool.request.common.constant.icons.CoolRequestIcons;
import com.cool.request.utils.ResourceBundleUtils;
import com.cool.request.view.main.MainTopTreeView;
import com.cool.request.view.tool.ProviderManager;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.ui.treeStructure.Tree;
import org.jetbrains.annotations.NotNull;

/**
 * @author caoayu
 */
public class CollapseAction extends BaseAnAction {
    public CollapseAction(Project project) {
        super(project, () -> ResourceBundleUtils.getString("collapse"),
                () -> ResourceBundleUtils.getString("collapse"), CoolRequestIcons.COLLAPSE);
    }


    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            return;
        }
        ProviderManager.findAndConsumerProvider(MainTopTreeView.class, project, mainTopTreeView -> {
            Tree tree = mainTopTreeView.getTree();
            for (int i = 0; i < tree.getRowCount(); i++) {
                tree.collapseRow(i);
            }
        });
    }

}
