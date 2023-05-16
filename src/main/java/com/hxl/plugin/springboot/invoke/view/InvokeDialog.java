package com.hxl.plugin.springboot.invoke.view;

import com.hxl.plugin.springboot.invoke.bean.RequestMappingInvokeBean;
import com.hxl.plugin.springboot.invoke.invoke.ControllerInvoke;
import com.hxl.plugin.springboot.invoke.invoke.InvokeResult;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class InvokeDialog extends DialogWrapper {
    private JTextField urlField;
    private JTextField paramField;
    private JRadioButton jsonRadioButton;
    private JRadioButton formRadioButton;
    private JRadioButton sourceButton;
    private JRadioButton proxyButton;

    private JTextArea resultTextArea;
    private RequestMappingInvokeBean requestMappingInvokeBean;
    private int port;
    private Callback callback;

    public InvokeDialog(RequestMappingInvokeBean requestMappingInvokeBean, int port, Callback callback) {
        super(false);
        this.requestMappingInvokeBean = requestMappingInvokeBean;
        this.port = port;
        this.callback = callback;
        setTitle("调用");
        init();

    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(5, 5, 5, 5);
        JLabel urlLabel = new JLabel("请求地址(可增加参数):");
        JLabel methodLabel = new JLabel("GET");
        urlField = new JTextField(20);
        urlField.setText(requestMappingInvokeBean.getUrl());

        constraints.gridx = 0;
        constraints.gridy = 0;
        panel.add(urlLabel, constraints);

        constraints.gridx = 2;
        constraints.gridy = 0;
        panel.add(methodLabel, constraints);

        constraints.gridx = 1;
        constraints.gridy = 0;
        panel.add(urlField, constraints);

        JPanel proxyJPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        proxyButton = new JRadioButton("代理对像");
        sourceButton = new JRadioButton("原对像");
        ButtonGroup proxyButtonGroup = new ButtonGroup();
        proxyButtonGroup.add(proxyButton);
        proxyButtonGroup.add(sourceButton);
        proxyJPanel.add(proxyButton);
        proxyJPanel.add(sourceButton);
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 2;
        sourceButton.setSelected(true);
        panel.add(proxyJPanel, constraints);


        JLabel requestBodyLabel = new JLabel("Post请求体:");
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 2;
        panel.add(requestBodyLabel, constraints);


        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        jsonRadioButton = new JRadioButton("JSON格式");
        formRadioButton = new JRadioButton("x-www-form-urlencoded格式");
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(jsonRadioButton);
        buttonGroup.add(formRadioButton);
        radioPanel.add(jsonRadioButton);
        radioPanel.add(formRadioButton);
        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.gridwidth = 2;
        jsonRadioButton.setSelected(true);
        panel.add(radioPanel, constraints);


        resultTextArea = new JTextArea(5, 30);
        resultTextArea.setEditable(true);
        constraints.gridx = 0;
        constraints.gridy = 4;
        constraints.gridwidth = 2;
        panel.add(new JScrollPane(resultTextArea), constraints);


        return panel;
    }

    @Override
    protected void doOKAction() {
        String url = urlField.getText();
        String contentType = jsonRadioButton.isSelected() ? "application/json" : "x-www-form-urlencoded";
        boolean useProxyObject = proxyButton.isSelected();
        ControllerInvoke.InvokeData invokeData = new ControllerInvoke.InvokeData(url, contentType, resultTextArea.getText(), requestMappingInvokeBean.getId(), useProxyObject);
        InvokeResult invoke = new ControllerInvoke(this.port).invoke(invokeData);
        callback.result(invoke);
        super.doOKAction();
    }

    @FunctionalInterface
    interface Callback {
        public void result(InvokeResult invokeResult);
    }
}



