package com.cool.request.action.copy;

import com.cool.request.icons.MyIcons;
import com.cool.request.openapi.OpenApiUtils;
import com.cool.request.utils.ClipboardUtils;
import com.cool.request.view.main.MainTopTreeView;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.util.ui.tree.TreeUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.tree.TreePath;

public class CopyCurlAnAction extends AnAction {
    private final MainTopTreeView mainTopTreeView;
    public CopyCurlAnAction(MainTopTreeView mainTopTreeView) {
        super("Curl");
        getTemplatePresentation().setIcon(MyIcons.CURL);
        this.mainTopTreeView = mainTopTreeView;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        TreePath selectedPathIfOne = TreeUtil.getSelectedPathIfOne(this.mainTopTreeView.getTree());
        if (selectedPathIfOne!=null && selectedPathIfOne.getLastPathComponent() instanceof MainTopTreeView.RequestMappingNode){
            MainTopTreeView.RequestMappingNode requestMappingNode = (MainTopTreeView.RequestMappingNode) selectedPathIfOne.getLastPathComponent();
            ClipboardUtils.copyToClipboard(OpenApiUtils.toCurl(mainTopTreeView.getProject(),requestMappingNode.getData()));
        }
    }
}
