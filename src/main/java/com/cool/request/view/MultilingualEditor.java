package com.cool.request.view;

import com.intellij.ide.highlighter.HtmlFileType;
import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.json.JsonFileType;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.EditorSettings;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypes;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.ui.EditorTextField;
import com.intellij.util.LocalTimeCounter;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.border.Border;

public class MultilingualEditor extends EditorTextField implements Disposable {
    public static final FileType TEXT_FILE_TYPE = FileTypes.PLAIN_TEXT;
    public static final FileType JSON_FILE_TYPE = JsonFileType.INSTANCE;
    public static final FileType HTML_FILE_TYPE = HtmlFileType.INSTANCE;
    public static final FileType XML_FILE_TYPE = XmlFileType.INSTANCE;

    public MultilingualEditor(Project project, FileType fileType) {
        super(null, project, fileType, false, false);
    }
    public static void setupTextFieldEditor(@NotNull EditorEx editor) {
        EditorSettings settings = editor.getSettings();
        settings.setFoldingOutlineShown(true);
        settings.setLineNumbersShown(true);
        settings.setIndentGuidesShown(true);
        editor.setHorizontalScrollbarVisible(true);
        editor.setVerticalScrollbarVisible(true);
    }

    @Override
    public void dispose() {
        if (getEditor()!=null){
            EditorFactory.getInstance().releaseEditor(getEditor());
        }
    }

    public void setText(@Nullable final String text, @NotNull final FileType fileType) {
        super.setFileType(fileType);
        Document document = createDocument(text, fileType);
        setDocument(document);
        PsiFile psiFile = PsiDocumentManager.getInstance(getProject()).getPsiFile(document);
        if (psiFile != null) {
            WriteCommandAction.runWriteCommandAction(
                    getProject(),
                    () -> {
                        CodeStyleManager.getInstance(getProject()).reformat(psiFile);
                    }
            );
        }
    }

    @Override
    public void setFileType(@NotNull FileType fileType) {
        setNewDocumentAndFileType(fileType, createDocument(getText(), fileType));
    }

    @Override
    protected Document createDocument() {
        return createDocument(null, getFileType());
    }

    private void initOneLineMode(@NotNull final EditorEx editor) {
        editor.setOneLineMode(false);
        editor.setColorsScheme(editor.createBoundColorSchemeDelegate(null));
        editor.getSettings().setCaretRowShown(false);
    }

    @Override
    protected EditorEx createEditor() {
        EditorEx editor = super.createEditor();
        initOneLineMode(editor);
        setupTextFieldEditor(editor);
        return editor;
    }

    @Override
    public void repaint(long tm, int x, int y, int width, int height) {
        super.repaint(tm, x, y, width, height);
        if (getEditor() instanceof EditorEx) {
            initOneLineMode(((EditorEx) getEditor()));
        }
    }

    @Override
    public void setBorder(Border border) {
        super.setBorder(JBUI.Borders.empty());
    }

    public Document createDocument(@Nullable final String text, @NotNull final FileType fileType) {
        final PsiFileFactory factory = PsiFileFactory.getInstance(getProject());
        final long stamp = LocalTimeCounter.currentTime();
        final PsiFile psiFile = factory.createFileFromText(
                "",
                fileType,
                text == null ? "" : text,
                stamp,
                true,
                false
        );
        return PsiDocumentManager.getInstance(getProject()).getDocument(psiFile);
    }
}