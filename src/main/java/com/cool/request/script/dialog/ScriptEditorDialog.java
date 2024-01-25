package com.cool.request.script.dialog;

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
        javaEditorTextField = new JavaEditorTextField(content, project);
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
