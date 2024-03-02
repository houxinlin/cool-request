package com.cool.request.view.editor;

import com.cool.request.common.bean.components.controller.Controller;
import com.cool.request.common.constant.CoolRequestConfigConstant;
import com.cool.request.view.component.MainBottomHTTPContainer;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorState;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.beans.PropertyChangeListener;

public class HTTPEditor implements FileEditor {
    private Project project;

    private VirtualFile virtualFile;

    public HTTPEditor(Project project, @NotNull VirtualFile file) {
        this.project = project;
        this.virtualFile = file;
    }

    @Override
    public @NotNull JComponent getComponent() {
        Controller controller = virtualFile.getUserData(CoolRequestConfigConstant.OpenHTTPREquestPageTabKey);
        return new MainBottomHTTPContainer(project, controller);
    }

    @Override
    public @Nullable JComponent getPreferredFocusedComponent() {
        return null;
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
        Disposer.dispose(this);
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
