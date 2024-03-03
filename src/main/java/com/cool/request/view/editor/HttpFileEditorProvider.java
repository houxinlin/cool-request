package com.cool.request.view.editor;

import com.cool.request.component.CoolRequestPluginDisposable;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorPolicy;
import com.intellij.openapi.fileEditor.FileEditorProvider;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class HttpFileEditorProvider implements FileEditorProvider, DumbAware {
    private static String EDITOR_TYPE_ID = "CoolRequestFileEditorProvider";

    @Override
    public boolean accept(@NotNull Project project, @NotNull VirtualFile file) {
        return file.getFileType().getDefaultExtension().equalsIgnoreCase("coolrequest")
                && file instanceof CoolHTTPRequestVirtualFile;
    }

    @Override
    public @NotNull FileEditor createEditor(@NotNull Project project, @NotNull VirtualFile file) {
        //        Disposer.register(CoolRequestPluginDisposable.getInstance(project), httpEditor);
        return new HTTPEditor(project, file);
    }

    @Override
    public @NotNull @NonNls String getEditorTypeId() {
        return EDITOR_TYPE_ID;
    }

    @Override
    public @NotNull FileEditorPolicy getPolicy() {
        return FileEditorPolicy.HIDE_DEFAULT_EDITOR;
    }
}
