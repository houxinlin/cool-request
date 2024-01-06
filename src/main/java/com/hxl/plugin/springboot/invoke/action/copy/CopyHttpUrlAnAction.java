package com.hxl.plugin.springboot.invoke.action.copy;

import com.hxl.plugin.springboot.invoke.Constant;
import com.hxl.plugin.springboot.invoke.bean.EmptyEnvironment;
import com.hxl.plugin.springboot.invoke.utils.ClipboardUtils;
import com.hxl.plugin.springboot.invoke.utils.StringUtils;
import com.hxl.plugin.springboot.invoke.view.main.MainTopTreeView;
import com.hxl.plugin.springboot.invoke.view.main.MainViewDataProvide;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.util.ui.tree.TreeUtil;
import icons.MyIcons;
import org.jetbrains.annotations.NotNull;

import javax.swing.tree.TreePath;
import java.util.StringJoiner;

public class CopyHttpUrlAnAction extends AnAction {
    private final MainTopTreeView mainTopTreeVi;
    private static final String HOST = "http://localhost:";

    public CopyHttpUrlAnAction(MainTopTreeView mainTopTreeView) {
        super("Http Url");
        getTemplatePresentation().setIcon(MyIcons.IC_HTTP);
        this.mainTopTreeVi =  mainTopTreeView;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        TreePath selectedPathIfOne = TreeUtil.getSelectedPathIfOne(this.mainTopTreeVi.getTree());
        if (selectedPathIfOne != null && selectedPathIfOne.getLastPathComponent() instanceof MainTopTreeView.RequestMappingNode) {
            MainTopTreeView.RequestMappingNode requestMappingNode = (MainTopTreeView.RequestMappingNode) selectedPathIfOne.getLastPathComponent();
            String base = HOST + requestMappingNode.getData().getServerPort() + requestMappingNode.getData().getContextPath();

            String url = StringUtils.joinUrlPath(base,requestMappingNode.getData().getController().getUrl());
            MainViewDataProvide mainViewDataProvide = mainTopTreeVi.getProject().getUserData(Constant.MainViewDataProvideKey);

            if (!((mainViewDataProvide.getSelectRequestEnvironment()) instanceof EmptyEnvironment)){
                url = mainViewDataProvide.applyUrl(requestMappingNode.getData());
            }
            ClipboardUtils.copyToClipboard(url);
        }
    }
}
