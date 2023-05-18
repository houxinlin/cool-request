package com.hxl.plugin.springboot.invoke.view.browse;

import com.hxl.plugin.springboot.invoke.utils.ResourceBundleUtils;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;


public class TextBrowse extends DialogWrapper {
    private String text;

    public TextBrowse(String text) {
        super(false);
        this.text = text;
        setSize(400,400);
        setTitle(ResourceBundleUtils.getString("response"));
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        JPanel jPanel = new JPanel(new BorderLayout());
        JTextArea jTextArea = new JTextArea(this.text);
        jPanel.add(jTextArea,BorderLayout.CENTER);
        return jPanel;
    }
}
