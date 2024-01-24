package com.cool.request.view.widget;

import com.intellij.lang.Language;
import com.intellij.lang.java.JavaLanguage;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.EditorSettings;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.ui.HorizontalScrollBarEditorCustomization;
import com.intellij.ui.LanguageTextField;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public class JavaEditorTextField extends LanguageTextField {
    //    public JavaEditorTextField(@NotNull String text, Project project) {
//        super(text, project, JavaFileType.INSTANCE);
//        setOneLineMode(false);
//    }
    public JavaEditorTextField(@NotNull String value, @Nullable Project project) {
        super(JavaLanguage.INSTANCE, project, value, new MyDocumentCreator());
        setOneLineMode(false);

    }

    private static class MyDocumentCreator extends SimpleDocumentCreator {
        @Override
        public Document createDocument(String value, @Nullable Language language, Project project) {
            Document document = super.createDocument(value, language, project);
            return document;
        }

        @Override
        public void customizePsiFile(PsiFile file) {
            super.customizePsiFile(file);
        }
    }

    @Override
    protected @NotNull EditorEx createEditor() {
        EditorEx editor = super.createEditor();
        setupTextFieldEditor(editor);
        EditorSettings settings = editor.getSettings();
        settings.setLineNumbersShown(true);
        settings.setFoldingOutlineShown(true);
        settings.setAllowSingleLogicalLineFolding(true);
        settings.setRightMarginShown(true);
        return editor;
    }

    public static void setupTextFieldEditor(@NotNull EditorEx editor) {
        editor.setHorizontalScrollbarVisible(true);
        editor.setVerticalScrollbarVisible(true);
        HorizontalScrollBarEditorCustomization.ENABLED.customize(editor);


    }

    @Override
    protected void paintChildren(Graphics g) {
        super.paintChildren(g);
        g.drawString("asd", 0, 0);
    }
}
