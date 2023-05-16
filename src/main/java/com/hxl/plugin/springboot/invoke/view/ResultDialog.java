package com.hxl.plugin.springboot.invoke.view;

import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class ResultDialog  extends DialogWrapper {
    @Override
    protected @Nullable JComponent createCenterPanel() {
        return null;
    }
    private String msg;
    public ResultDialog(String msg) {
        super(true); // 设置对话框为模态对话框
        init();
        setTitle("My Dialog");
        this.msg =msg;
    }
    // 在构造函数中调用该方法来初始化主要面板
    private void createUIComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        JTextArea  textField = new JTextArea();
        mainPanel.add(textField, BorderLayout.CENTER);
    }
}
