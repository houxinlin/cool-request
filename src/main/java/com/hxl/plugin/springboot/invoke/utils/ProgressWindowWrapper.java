package com.hxl.plugin.springboot.invoke.utils;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.progress.util.ProgressWindow;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ProgressWindowWrapper extends ProgressWindow {
    public ProgressWindowWrapper(boolean shouldShowCancel, @Nullable Project project) {
        super(shouldShowCancel, project);
    }

    public static ProgressWindowWrapper newProgressWindowWrapper(@Nullable Project project){
        return new ProgressWindowWrapper(true, project);
    }
    public  void  run(@NotNull Task task){
        setTitle(task.getTitle());
        start();
        ProgressManager.getInstance().run(new Task.Backgroundable(task.getProject(), task.getTitle()) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                task.run(indicator);
                stop();
            }
        });

    }
}
