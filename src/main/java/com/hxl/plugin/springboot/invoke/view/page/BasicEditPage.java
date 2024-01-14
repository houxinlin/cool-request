package com.hxl.plugin.springboot.invoke.view.page;

import com.hxl.plugin.springboot.invoke.view.MultilingualEditor;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;

import javax.swing.*;
import java.awt.*;

public abstract class BasicEditPage  extends JPanel {
    private final Project project;
    private MultilingualEditor editor;

    public abstract FileType getFileType();
    public BasicEditPage(Project project) {
        this.project = project;
        editor = new MultilingualEditor(this.project, getFileType());
        setLayout(new BorderLayout());
        add(editor,BorderLayout.CENTER);
    }
    public void setText(String text){
        editor.setText(text);
    }

    public String getText(){
        return editor.getText();
    }
}
