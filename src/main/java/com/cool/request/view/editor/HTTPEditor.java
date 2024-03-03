package com.cool.request.view.editor;

import com.cool.request.common.bean.components.controller.Controller;
import com.cool.request.common.constant.CoolRequestConfigConstant;
import com.cool.request.view.component.MainBottomHTTPContainer;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorState;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.beans.PropertyChangeListener;

public class HTTPEditor implements FileEditor {
    private final VirtualFile virtualFile;
    private final MainBottomHTTPContainer mainBottomHTTPContainer;
    private Project project;
    private @NotNull VirtualFile file;

    public HTTPEditor(Project project, @NotNull VirtualFile file) {
        this.virtualFile = file;
        this.project = project;
        this.file = file;
        this.mainBottomHTTPContainer = new MainBottomHTTPContainer(project,
                ((CoolHTTPRequestVirtualFile) file).getController(), this);
    }

    @Override
    public @NotNull JComponent getComponent() {
        return mainBottomHTTPContainer;
    }

    @Override
    public @Nullable JComponent getPreferredFocusedComponent() {
        return mainBottomHTTPContainer;
    }

    @Override
    public @Nls(capitalization = Nls.Capitalization.Title) @NotNull String getName() {
        Controller controller = virtualFile.getUserData(CoolRequestConfigConstant.OpenHTTPREquestPageTabKey);
        return controller.getUrl();
    }

    @Override
    public void setState(@NotNull FileEditorState state) {

    }

    @Override
    public boolean isModified() {
        return false;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public void addPropertyChangeListener(@NotNull PropertyChangeListener listener) {

    }

    @Override
    public void removePropertyChangeListener(@NotNull PropertyChangeListener listener) {

    }

    @Override
    public void dispose() {
//        FileEditorManager.getInstance(project).closeFile(this.file);
//        System.out.println("dispose");
//        Disposer.dispose(this);
    }

    @Override
    public <T> @Nullable T getUserData(@NotNull Key<T> key) {
        return null;
    }

    @Override
    public <T> void putUserData(@NotNull Key<T> key, @Nullable T value) {

    }

    @Override
    public VirtualFile getFile() {
        return this.virtualFile;
    }
}
