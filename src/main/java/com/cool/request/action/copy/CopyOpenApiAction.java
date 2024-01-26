package com.cool.request.action.copy;

import com.cool.request.common.constant.icons.CoolRequestIcons;
import com.cool.request.lib.openapi.OpenApiUtils;
import com.cool.request.utils.ClipboardUtils;
import com.cool.request.view.main.MainTopTreeView;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.util.ui.tree.TreeUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.tree.TreePath;
import java.util.List;

public class CopyOpenApiAction extends AnAction {
    private final MainTopTreeView mainTopTreeView;

    public CopyOpenApiAction(MainTopTreeView mainTopTreeView) {
        super("Openapi");
        getTemplatePresentation().setIcon(CoolRequestIcons.OPENAPI);
        this.mainTopTreeView = mainTopTreeView;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        TreePath selectedPathIfOne = TreeUtil.getSelectedPathIfOne(this.mainTopTreeView.getTree());
        if (selectedPathIfOne != null && selectedPathIfOne.getLastPathComponent() instanceof MainTopTreeView.RequestMappingNode) {
            String openApiJson = OpenApiUtils.toOpenApiJson(this.mainTopTreeView.getProject(), List.of(((MainTopTreeView.RequestMappingNode) selectedPathIfOne.getLastPathComponent()).getData()));
            ClipboardUtils.copyToClipboard(openApiJson);
        }
    }
}
