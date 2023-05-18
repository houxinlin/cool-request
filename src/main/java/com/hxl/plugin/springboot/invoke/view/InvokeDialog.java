package com.hxl.plugin.springboot.invoke.view;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hxl.plugin.springboot.invoke.Constant;
import com.hxl.plugin.springboot.invoke.bean.ControllerSetting;
import com.hxl.plugin.springboot.invoke.bean.RequestMappingInvokeBean;
import com.hxl.plugin.springboot.invoke.invoke.ControllerInvoke;
import com.hxl.plugin.springboot.invoke.invoke.InvokeResult;
import com.hxl.plugin.springboot.invoke.utils.ResourceBundleUtils;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class InvokeDialog extends DialogWrapper {
    private JTextField urlField;
    private JTextField paramField;
    private JRadioButton jsonRadioButton;
    private JRadioButton formRadioButton;
    private JRadioButton sourceButton;
    private JRadioButton proxyButton;
    private JCheckBox interceptor;
    private JTextArea resultTextArea;
    private RequestMappingInvokeBean requestMappingInvokeBean;
    private int port;
    private Callback callback;

    private JLabel methodLabel;

    public InvokeDialog(RequestMappingInvokeBean requestMappingInvokeBean, int port, Callback callback) {
        super(false);
        this.requestMappingInvokeBean = requestMappingInvokeBean;
        this.port = port;
        this.callback = callback;
        setModal(false);
        setTitle(ResourceBundleUtils.getString("invoke"));
        init();
        loadConfig();

    }
    private void saveConfig(){
        createIsNotExist();
        ControllerSetting controllerSetting = new ControllerSetting();
        controllerSetting.setBody(resultTextArea.getText());
        controllerSetting.setUrl(urlField.getText());
        controllerSetting.setJsonContent(jsonRadioButton.isSelected());
        controllerSetting.setUseProxy(proxyButton.isSelected());
        controllerSetting.setUseInterceptor(interceptor.isSelected());
        try {
            Path path = Paths.get(Constant.CONFIG_CONTROLLER_SETTING.toString(), this.requestMappingInvokeBean.getId());

            Files.write(path,new ObjectMapper().writeValueAsString(controllerSetting).getBytes());
        } catch (IOException ignored) {
        }
    }
    private void createIsNotExist(){
        if (!Files.exists(Constant.CONFIG_CONTROLLER_SETTING)) {
            try {
                Files.createDirectory(Constant.CONFIG_CONTROLLER_SETTING);
            } catch (IOException ignored) {
            }
        }
    }
    private void loadConfig(){
        createIsNotExist();
        Path path = Paths.get(Constant.CONFIG_CONTROLLER_SETTING.toString(), this.requestMappingInvokeBean.getId());
        if (!Files.exists(path))return;
        try {
            ControllerSetting controllerSetting = new ObjectMapper().readValue(new String(Files.readAllBytes(path)), ControllerSetting.class);
            urlField.setText(controllerSetting.getUrl());
            resultTextArea.setText(controllerSetting.getBody());
            jsonRadioButton.setSelected(controllerSetting.getJsonContent());
            formRadioButton.setSelected(!controllerSetting.getJsonContent());

            proxyButton.setSelected(controllerSetting.getUseProxy());
            sourceButton.setSelected(!controllerSetting.getUseProxy());

            interceptor.setSelected(controllerSetting.getUseInterceptor());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = JBUI.insets(5);
        JLabel urlLabel = new JLabel(ResourceBundleUtils.getString("request.address"));
         methodLabel = new JLabel("GET");
        urlField = new JTextField(20);
        urlField.setText(requestMappingInvokeBean.getUrl());
        methodLabel.setText(requestMappingInvokeBean.getHttpMethod());

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
        proxyButton = new JRadioButton(ResourceBundleUtils.getString("proxy.object"));
        sourceButton = new JRadioButton(ResourceBundleUtils.getString("source.object"));
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

        interceptor = new JCheckBox(ResourceBundleUtils.getString("interceptor"));
        JPanel webJPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        webJPanel.add(interceptor);
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 2;
        panel.add(webJPanel, constraints);


        JLabel requestBodyLabel = new JLabel(ResourceBundleUtils.getString("post.body"));
        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.gridwidth = 2;
        panel.add(requestBodyLabel, constraints);


        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        jsonRadioButton = new JRadioButton("json");
        formRadioButton = new JRadioButton("x-www-form-urlencoded");
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(jsonRadioButton);
        buttonGroup.add(formRadioButton);
        radioPanel.add(jsonRadioButton);
        radioPanel.add(formRadioButton);
        constraints.gridx = 0;
        constraints.gridy = 5;
        constraints.gridwidth = 2;
        jsonRadioButton.setSelected(true);
        panel.add(radioPanel, constraints);




        resultTextArea = new JTextArea(5, 30);
        resultTextArea.setEditable(true);
        constraints.gridx = 0;
        constraints.gridy = 6;
        constraints.gridwidth = 2;
        panel.add(new JScrollPane(resultTextArea), constraints);


        return panel;
    }

    @Override
    protected void doOKAction() {
        String url = urlField.getText();
        String contentType = jsonRadioButton.isSelected() ? "application/json" : "x-www-form-urlencoded";
        boolean useProxyObject = proxyButton.isSelected();
        ControllerInvoke.InvokeData invokeData = new ControllerInvoke.InvokeData(url, contentType,
                resultTextArea.getText(), requestMappingInvokeBean.getId(),
                useProxyObject,interceptor.isSelected(),false);
        InvokeResult invoke = new ControllerInvoke(this.port).invoke(invokeData);
        callback.result(invoke);
        saveConfig();

//        try {
//            JsonBrowse jsonBrowse = new JsonBrowse(Paths.get("/home/LinuxWork/test/JsonBrowse/a.json"));
//            jsonBrowse.show();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
        super.doOKAction();
    }

    @FunctionalInterface
    interface Callback {
        public void result(InvokeResult invokeResult);
    }
}



