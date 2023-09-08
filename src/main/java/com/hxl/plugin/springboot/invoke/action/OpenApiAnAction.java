package com.hxl.plugin.springboot.invoke.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.ui.treeStructure.SimpleTree;
import icons.MyIcons;
import org.jetbrains.annotations.NotNull;

public class OpenApiAnAction  extends AnAction {
    public OpenApiAnAction(SimpleTree tree) {
        super("openapi", "openapi", MyIcons.OPENAPI);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

    }
}
