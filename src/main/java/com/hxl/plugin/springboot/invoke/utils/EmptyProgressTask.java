package com.hxl.plugin.springboot.invoke.utils;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import org.jetbrains.annotations.NotNull;

public class EmptyProgressTask  extends Task.Backgroundable {
    public EmptyProgressTask( String title) {
        super(ProjectUtils.getCurrentProject(), title);
    }
    @Override
    public void run(@NotNull ProgressIndicator indicator) {

    }
}
