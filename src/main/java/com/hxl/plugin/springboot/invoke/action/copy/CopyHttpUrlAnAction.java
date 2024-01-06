package com.hxl.plugin.springboot.invoke.action.copy;

import com.hxl.plugin.springboot.invoke.Constant;
import com.hxl.plugin.springboot.invoke.utils.ClipboardUtils;
import com.hxl.plugin.springboot.invoke.view.main.MainTopTreeView;
import com.hxl.plugin.springboot.invoke.view.main.MainViewDataProvide;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.util.ui.tree.TreeUtil;
import icons.MyIcons;
import org.jetbrains.annotations.NotNull;

import javax.swing.tree.TreePath;

public class CopyHttpUrlAnAction extends AnAction {
    private final MainTopTreeView mainTopTreeVi;

    public CopyHttpUrlAnAction(MainTopTreeView mainTopTreeView) {
        super("Http Url");
        getTemplatePresentation().setIcon(MyIcons.IC_HTTP);
        this.mainTopTreeVi = mainTopTreeView;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        TreePath selectedPathIfOne = TreeUtil.getSelectedPathIfOne(this.mainTopTreeVi.getTree());
        if (selectedPathIfOne != null && selectedPathIfOne.getLastPathComponent() instanceof MainTopTreeView.RequestMappingNode) {
            MainTopTreeView.RequestMappingNode requestMappingNode = (MainTopTreeView.RequestMappingNode) selectedPathIfOne.getLastPathComponent();

            MainViewDataProvide mainViewDataProvide = mainTopTreeVi.getProject().getUserData(Constant.MainViewDataProvideKey);

            String url = mainViewDataProvide.applyUrl(requestMappingNode.getData());
            ClipboardUtils.copyToClipboard(url);
        }
    }
}
