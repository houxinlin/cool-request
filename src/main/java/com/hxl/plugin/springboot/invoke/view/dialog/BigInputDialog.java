package com.hxl.plugin.springboot.invoke.view.dialog;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBTextArea;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class BigInputDialog  extends DialogWrapper {
    private final JBTextArea textArea =new JBTextArea();
    public BigInputDialog(@Nullable Project project) {
        super(project);
        setSize(600,600);
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return textArea;
    }
    public String getValue(){
        return  textArea.getText();
    }
}
