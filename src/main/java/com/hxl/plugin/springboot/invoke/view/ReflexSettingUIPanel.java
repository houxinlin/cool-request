package com.hxl.plugin.springboot.invoke.view;

import com.hxl.plugin.springboot.invoke.bean.BeanInvokeSetting;
import com.hxl.plugin.springboot.invoke.model.SpringMvcRequestMappingSpringInvokeEndpoint;
import com.hxl.plugin.springboot.invoke.springmvc.RequestCache;
import com.hxl.plugin.springboot.invoke.utils.RequestParamCacheManager;
import com.hxl.plugin.springboot.invoke.utils.ResourceBundleUtils;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import java.awt.*;

public class ReflexSettingUIPanel extends JPanel {
    private final JRadioButton sourceButton;
    private final JRadioButton proxyButton;
    private final JCheckBox interceptor;
    private SpringMvcRequestMappingSpringInvokeEndpoint springMvcRequestMappingEndpoint;

    public ReflexSettingUIPanel() {
        super(new BorderLayout());
        JPanel panel = new JPanel(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = JBUI.insets(5);

        JPanel proxyJPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        proxyButton = new JRadioButton(ResourceBundleUtils.getString("proxy.object"));
        sourceButton = new JRadioButton(ResourceBundleUtils.getString("source.object"));
        ButtonGroup proxyButtonGroup = new ButtonGroup();
        proxyButtonGroup.add(proxyButton);
        proxyButtonGroup.add(sourceButton);
        proxyJPanel.add(proxyButton);
        proxyJPanel.add(sourceButton);
        constraints.gridx = 0;
        constraints.gridy = 0;
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

        add(panel, BorderLayout.CENTER);
    }

    public void setRequestMappingInvokeBean(SpringMvcRequestMappingSpringInvokeEndpoint springMvcRequestMappingEndpoint) {
        this.springMvcRequestMappingEndpoint = springMvcRequestMappingEndpoint;
        loadConfig();
    }

    private void loadConfig() {
        RequestCache cache = RequestParamCacheManager.getCache(this.springMvcRequestMappingEndpoint.getId());
        proxyButton.setSelected(cache != null && cache.isUseProxy());
        sourceButton.setSelected(cache != null && !cache.isUseProxy());
        interceptor.setSelected(cache != null && cache.isUseInterceptor());
    }

    public BeanInvokeSetting getBeanInvokeSetting() {
        BeanInvokeSetting beanInvokeSetting = new BeanInvokeSetting();
        beanInvokeSetting.setUseInterceptor(interceptor.isSelected());
        beanInvokeSetting.setUseProxy(proxyButton.isSelected());
        return beanInvokeSetting;
    }

}



