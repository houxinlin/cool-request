package com.cool.request.action.copy;

import com.cool.request.common.constant.CoolRequestConfigConstant;
import com.cool.request.common.icons.CoolRequestIcons;
import com.cool.request.view.main.MainTopTreeView;
import com.cool.request.view.main.RequestEnvironmentProvide;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.util.ui.tree.TreeUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.tree.TreePath;

public class CopyHttpUrlAnAction extends AnAction {
    private final MainTopTreeView mainTopTreeVi;

    public CopyHttpUrlAnAction(MainTopTreeView mainTopTreeView) {
        super("Http Url");
        getTemplatePresentation().setIcon(CoolRequestIcons.IC_HTTP);
        this.mainTopTreeVi = mainTopTreeView;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        TreePath selectedPathIfOne = TreeUtil.getSelectedPathIfOne(this.mainTopTreeVi.getTree());
        if (selectedPathIfOne != null && selectedPathIfOne.getLastPathComponent() instanceof MainTopTreeView.RequestMappingNode) {
            MainTopTreeView.RequestMappingNode requestMappingNode = (MainTopTreeView.RequestMappingNode) selectedPathIfOne.getLastPathComponent();

            RequestEnvironmentProvide requestEnvironmentProvide = mainTopTreeVi.getProject().getUserData(CoolRequestConfigConstant.RequestEnvironmentProvideKey);

//            String url = mainViewDataProvide.applyUrl(requestMappingNode.getData());
//            ClipboardUtils.copyToClipboard(url);
        }
    }
}
