package com.cool.request.action.copy;

import com.cool.request.utils.ClipboardUtils;
import com.cool.request.utils.ResourceBundleUtils;
import com.cool.request.view.main.MainTopTreeView;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.ui.treeStructure.SimpleTree;
import com.intellij.util.ui.tree.TreeUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.tree.TreePath;

public class CopyClassNameAnAction extends AnAction {
    private final SimpleTree simpleTree;

    public CopyClassNameAnAction(MainTopTreeView mainTopTreeView) {
        super(ResourceBundleUtils.getString("class.name"));
        getTemplatePresentation().setIcon(AllIcons.Nodes.Class);
        this.simpleTree = ((SimpleTree) mainTopTreeView.getTree());
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        TreePath selectedPathIfOne = TreeUtil.getSelectedPathIfOne(this.simpleTree);
        if (selectedPathIfOne != null && selectedPathIfOne.getLastPathComponent() instanceof MainTopTreeView.RequestMappingNode) {
            MainTopTreeView.RequestMappingNode requestMappingNode = (MainTopTreeView.RequestMappingNode) selectedPathIfOne.getLastPathComponent();
            ClipboardUtils.copyToClipboard(requestMappingNode.getData().getSimpleClassName());
        }
    }
}
