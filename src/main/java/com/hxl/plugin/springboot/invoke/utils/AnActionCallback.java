package com.hxl.plugin.springboot.invoke.utils;

import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface AnActionCallback {
    void actionPerformed(@NotNull AnActionEvent e);
}
