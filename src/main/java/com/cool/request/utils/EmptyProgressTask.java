package com.cool.request.utils;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class EmptyProgressTask  extends Task.Backgroundable {
    public EmptyProgressTask(Project project, String title) {
        super(project, title);
    }
    @Override
    public void run(@NotNull ProgressIndicator indicator) {

    }
}
