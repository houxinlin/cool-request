package com.hxl.plugin.springboot.invoke.action.copy;

import com.hxl.plugin.springboot.invoke.model.RequestMappingModel;
import com.hxl.plugin.springboot.invoke.openapi.OpenApiUtils;
import com.hxl.plugin.springboot.invoke.plugin.apifox.ApiFoxExport;
import com.hxl.plugin.springboot.invoke.utils.CursorUtils;
import com.hxl.plugin.springboot.invoke.view.main.MainTopTreeView;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.treeStructure.SimpleTree;
import icons.MyIcons;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public class CopyClassNameAnAction extends AnAction {
    private final SimpleTree simpleTree;
    private final ApiFoxExport apifoxExp = new ApiFoxExport();
    private MainTopTreeView mainTopTreeView;

    public CopyClassNameAnAction(MainTopTreeView mainTopTreeView) {
        super("class name", "class name", MyIcons.APIFOX);
        this.simpleTree = ((SimpleTree) mainTopTreeView.getTree());
        this.mainTopTreeView = mainTopTreeView;
    }


    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

    }
}
