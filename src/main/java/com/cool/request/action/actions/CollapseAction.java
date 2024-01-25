package com.cool.request.action.actions;

import com.cool.request.tool.ProviderManager;
import com.cool.request.utils.ResourceBundleUtils;
import com.cool.request.view.component.ApiToolPage;
import com.cool.request.view.main.MainTopTreeView;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.treeStructure.Tree;
import icons.MyIcons;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Consumer;

import static com.cool.request.Constant.PLUGIN_ID;

/**
 * @author caoayu
 */
public class CollapseAction extends BaseAnAction {
    public CollapseAction(Project project) {
        super(project, () -> ResourceBundleUtils.getString("collapse"),
                () -> ResourceBundleUtils.getString("collapse"), MyIcons.COLLAPSE);
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
