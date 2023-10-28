package com.hxl.plugin.springboot.invoke.action.copy;

import com.hxl.plugin.springboot.invoke.openapi.OpenApiUtils;
import com.hxl.plugin.springboot.invoke.utils.ClipboardUtils;
import com.hxl.plugin.springboot.invoke.view.main.MainTopTreeView;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.ui.treeStructure.SimpleTree;
import com.intellij.util.ui.tree.TreeUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.tree.TreePath;
import java.util.List;

public class CopyOpenApiAction extends AnAction {
    private final SimpleTree simpleTree;

    public CopyOpenApiAction(MainTopTreeView mainTopTreeView) {
        super("Openapi");
        this.simpleTree = ((SimpleTree) mainTopTreeView.getTree());
    }


    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        TreePath selectedPathIfOne = TreeUtil.getSelectedPathIfOne(this.simpleTree);
        if (selectedPathIfOne!=null && selectedPathIfOne.getLastPathComponent() instanceof MainTopTreeView.RequestMappingNode){
            String openApiJson = OpenApiUtils.toOpenApiJson(List.of(((MainTopTreeView.RequestMappingNode) selectedPathIfOne.getLastPathComponent()).getData()));
            ClipboardUtils.copyToClipboard(openApiJson);
        }
    }
}
