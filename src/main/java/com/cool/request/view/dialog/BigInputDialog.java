package com.cool.request.view.dialog;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class BigInputDialog extends DialogWrapper {
    private JPanel root;
    private JTextArea textArea;
    private JLabel tip;

    public BigInputDialog(@Nullable Project project, String tip) {
        super(project);
        this.tip.setText(tip);
        setSize(600,400);
        init();
    }

    public BigInputDialog(@Nullable Project project) {
        super(project);
        this.tip.setText("");
        setSize(600,400);
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        textArea.setLineWrap(true);
        return root;
    }

    public String getValue() {
        return textArea.getText();
    }
}
