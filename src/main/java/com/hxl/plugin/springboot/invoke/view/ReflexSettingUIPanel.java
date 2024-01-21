package com.hxl.plugin.springboot.invoke.view;

import com.hxl.plugin.springboot.invoke.IdeaTopic;
import com.hxl.plugin.springboot.invoke.bean.BeanInvokeSetting;
import com.hxl.plugin.springboot.invoke.springmvc.RequestCache;
import com.hxl.plugin.springboot.invoke.utils.ResourceBundleUtils;
import com.intellij.openapi.application.ApplicationManager;

import javax.swing.*;

public class ReflexSettingUIPanel {
    private JRadioButton sourceButton;
    private JRadioButton proxyButton;
    private JCheckBox interceptor;
    private JLabel interceptorDesc;
    private JPanel root;

    public ReflexSettingUIPanel() {
        ApplicationManager.getApplication().getMessageBus().connect().subscribe(IdeaTopic.COOL_REQUEST_SETTING_CHANGE, (IdeaTopic.BaseListener) () -> loadText());
        loadText();
    }

    public JPanel getRoot() {
        return root;
    }

    private void loadText() {
        proxyButton.setText(ResourceBundleUtils.getString("proxy.object"));
        sourceButton.setText(ResourceBundleUtils.getString("source.object"));
        interceptor.setText(ResourceBundleUtils.getString("use.interceptor"));
        interceptorDesc.setText(ResourceBundleUtils.getString("use.interceptor.desc"));

        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(proxyButton);
        buttonGroup.add(sourceButton);

        proxyButton.setSelected(true);
    }

    public BeanInvokeSetting getBeanInvokeSetting() {
        BeanInvokeSetting beanInvokeSetting = new BeanInvokeSetting();
        beanInvokeSetting.setUseInterceptor(interceptor.isSelected());
        beanInvokeSetting.setUseProxy(proxyButton.isSelected());
        return beanInvokeSetting;
    }

    public void setRequestInfo(RequestCache requestCache) {
        proxyButton.setSelected(!requestCache.isUseProxy());
        sourceButton.setSelected(requestCache.isUseInterceptor());
        interceptor.setSelected(requestCache.isUseInterceptor());
    }
}



