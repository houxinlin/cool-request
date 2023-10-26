package com.hxl.plugin.springboot.invoke.action.copy;

import com.hxl.plugin.springboot.invoke.plugin.apifox.ApiFoxExport;
import com.hxl.plugin.springboot.invoke.view.main.MainTopTreeView;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.ui.treeStructure.SimpleTree;
import icons.MyIcons;
import org.jetbrains.annotations.NotNull;

public class CopyHttpUrlAnAction extends AnAction {
    private final SimpleTree simpleTree;
    private final ApiFoxExport apifoxExp = new ApiFoxExport();
    private MainTopTreeView mainTopTreeView;

    public CopyHttpUrlAnAction(MainTopTreeView mainTopTreeView) {
        super("http url", "http url", MyIcons.APIFOX);
        this.simpleTree = ((SimpleTree) mainTopTreeView.getTree());
        this.mainTopTreeView = mainTopTreeView;
    }


    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

    }
}
