package com.hxl.plugin.springboot.invoke.action.actions;

import com.hxl.plugin.springboot.invoke.utils.ResourceBundleUtils;
import com.hxl.plugin.springboot.invoke.view.CoolIdeaPluginWindowView;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.treeStructure.Tree;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static com.hxl.plugin.springboot.invoke.Constant.PLUGIN_ID;

/**
 * @author caoayu
 */
public class ExpandAction extends BaseAnAction {

    public ExpandAction(Project project) {
        super(project, () -> ResourceBundleUtils.getString("expand"), () -> ResourceBundleUtils.getString("expand"), AllIcons.Actions.Expandall);
    }


    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            return;
        }
        ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow(PLUGIN_ID);
        CoolIdeaPluginWindowView cool = (CoolIdeaPluginWindowView) Objects.requireNonNull(toolWindow.getContentManager().getContent(0)).getComponent();
        Tree tree = cool.getMainTopTreeView().getTree();
        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.expandRow(i);
        }
    }

}
