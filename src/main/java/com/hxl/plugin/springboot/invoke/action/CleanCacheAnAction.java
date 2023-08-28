package com.hxl.plugin.springboot.invoke.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class CleanCacheAnAction  extends AnAction {
    public CleanCacheAnAction() {
        super("clear call cache");
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

    }
}
