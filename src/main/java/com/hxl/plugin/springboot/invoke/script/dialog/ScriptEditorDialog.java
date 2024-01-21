package com.hxl.plugin.springboot.invoke.script.dialog;

import com.hxl.plugin.springboot.invoke.utils.ClassResourceUtils;
import com.hxl.plugin.springboot.invoke.view.widget.JavaEditorTextField;
import com.intellij.lang.java.JavaLanguage;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorSettings;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.HorizontalScrollBarEditorCustomization;
import com.intellij.ui.LanguageTextField;
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

    public ScriptEditorDialog(@Nullable Project project) {
        super(project);
        byte[] requestScriptBytes = ClassResourceUtils.read("/plugin-script-request.java");
        javaEditorTextField = new JavaEditorTextField(project, new String(requestScriptBytes));

//        jPanel.add(languageTextField, BorderLayout.CENTER);
        setSize(800, 800);
        init();
    }


    @Override
    protected @Nullable JComponent createCenterPanel() {
        return new JBScrollPane(javaEditorTextField);
    }
}
