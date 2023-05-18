package com.hxl.plugin.springboot.invoke.view.browse;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.hxl.plugin.springboot.invoke.utils.ResourceBundleUtils;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;


public class TextBrowse extends DialogWrapper {
    private String text;

    public TextBrowse(String text) {
        super(false);
        this.text = text;
        setSize(400,400);
        setTitle(ResourceBundleUtils.getString("response"));
        setOKButtonText(ResourceBundleUtils.getString("close.and.copy"));
        init();
    }
    @Override
    protected void doOKAction() {
        super.doOKAction();
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection selection = new StringSelection(this.text);
        clipboard.setContents(selection, null);
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        JPanel jPanel = new JPanel(new BorderLayout());
        JTextArea jTextArea = new JTextArea(this.text);
        jPanel.add(jTextArea,BorderLayout.CENTER);
        return jPanel;
    }
}
