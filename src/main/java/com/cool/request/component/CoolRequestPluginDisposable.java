package com.cool.request.component;


import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

@Service({Service.Level.APP, Service.Level.PROJECT})
public final class CoolRequestPluginDisposable implements Disposable {
    public static @NotNull Disposable getInstance() {
        return ApplicationManager.getApplication().getService(CoolRequestPluginDisposable.class);
    }

    public static @NotNull Disposable getInstance(@NotNull Project project) {
        return project.getService(CoolRequestPluginDisposable.class);
    }

    @Override
    public void dispose() {
    }
}