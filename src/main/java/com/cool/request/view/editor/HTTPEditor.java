/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * HTTPEditor.java is part of Cool Request
 *
 * License: GPL-3.0+
 *
 * Cool Request is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Cool Request is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Cool Request.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.cool.request.view.editor;

import com.cool.request.view.component.MainBottomHTTPContainer;
import com.cool.request.view.component.TabMainBottomHTTPContainer;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorLocation;
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
import java.util.HashMap;
import java.util.Map;

public class HTTPEditor implements FileEditor {
    private final VirtualFile virtualFile;
    private final MainBottomHTTPContainer mainBottomHTTPContainer;
    private final Map<Key<?>, Object> userData = new HashMap<>();

    public HTTPEditor(Project project, @NotNull VirtualFile file) {
        this.virtualFile = file;
        this.mainBottomHTTPContainer = new TabMainBottomHTTPContainer(project,
                ((CoolHTTPRequestVirtualFile) file).getController());
        Disposer.register(this, mainBottomHTTPContainer);
    }

    @Override
    public void dispose() {
        userData.clear();
    }

    @Override
    public @Nullable FileEditorLocation getCurrentLocation() {
        return null;
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
        return ((CoolHTTPRequestVirtualFile) virtualFile).getController().getUrl();
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
    public <T> @Nullable T getUserData(@NotNull Key<T> key) {
        return (T) userData.get(key);
    }

    @Override
    public <T> void putUserData(@NotNull Key<T> key, @Nullable T value) {
        userData.put(key, value);
    }

    @Override
    public VirtualFile getFile() {
        return this.virtualFile;
    }
}
