package com.cool.request.view.tool;

import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface AnActionCallback {
    void actionPerformed(@NotNull AnActionEvent e);
}
