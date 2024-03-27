/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * ScriptEditorDialog.java is part of Cool Request
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

package com.cool.request.components.http.script.dialog;

import com.cool.request.view.widget.JavaEditorTextField;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class ScriptEditorDialog extends DialogWrapper {
    private JBPanel jPanel = new JBPanel(new BorderLayout());

    private JavaEditorTextField javaEditorTextField;

    private void initOneLineMode(@NotNull final EditorEx editor) {
        editor.setOneLineMode(false);
        editor.setColorsScheme(editor.createBoundColorSchemeDelegate(null));
        editor.getSettings().setCaretRowShown(false);
    }

    public ScriptEditorDialog(String content, @Nullable Project project, java.util.function.Consumer<String> consumer) {
        super(project);
        javaEditorTextField = new JavaEditorTextField(project);
        javaEditorTextField.setText(content);
        javaEditorTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void documentChanged(@NotNull DocumentEvent event) {
                DocumentListener.super.documentChanged(event);
                consumer.accept(event.getDocument().getText());
            }
        });
        setSize(900, 600);
        init();
    }


    @Override
    protected @Nullable JComponent createCenterPanel() {
        return new JBScrollPane(javaEditorTextField);
    }
}
