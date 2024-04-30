package com.cool.request.scan;

import com.cool.request.common.bean.components.StaticComponent;
import com.cool.request.components.ComponentType;
import com.cool.request.view.tool.UserProjectManager;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicBoolean;

public class CoolRequestScan {
    private static final AtomicBoolean refreshLock = new AtomicBoolean();

    public static void staticScan(@NotNull Project project) {
        if (refreshLock.get()) return;
        ProgressManager.getInstance().run(new Task.Backgroundable(project, "Cool Request scan ...") {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                //标记所有静态组件不可用，可能会删除已有的
                refreshLock.set(true);
                try {
                    UserProjectManager.getInstance(project).markComponentState(StaticComponent.class, false);
                    ApplicationManager.getApplication().runReadAction((Computable<Object>) () -> {
                        UserProjectManager.getInstance(project).addComponent(ComponentType.CONTROLLER,
                                Scans.getInstance(project).scanController(project));
                        UserProjectManager.getInstance(project).addComponent(ComponentType.SCHEDULE,
                                Scans.getInstance(project).scanScheduled(project));
                        UserProjectManager.getInstance(project).deleteNotAvailableComponent(StaticComponent.class);
                        return null;
                    });
                } finally {
                    refreshLock.set(false);
                }
            }
        });
    }
}
