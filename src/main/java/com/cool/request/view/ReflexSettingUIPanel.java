/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * ReflexSettingUIPanel.java is part of Cool Request
 *
 * License: GPL-3.0+
 *
 * Cool Request is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Cool Request is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Cool Request.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.cool.request.view;

import com.cool.request.common.bean.BeanInvokeSetting;
import com.cool.request.common.constant.CoolRequestIdeaTopic;
import com.cool.request.components.CoolRequestPluginDisposable;
import com.cool.request.lib.springmvc.RequestCache;
import com.cool.request.utils.ResourceBundleUtils;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.util.messages.MessageBusConnection;

import javax.swing.*;

public class ReflexSettingUIPanel {
    private JRadioButton sourceButton;
    private JRadioButton proxyButton;
    private JCheckBox interceptor;
    private JLabel interceptorDesc;
    private JPanel root;

    public ReflexSettingUIPanel(Project project) {
        MessageBusConnection connect = ApplicationManager.getApplication().getMessageBus().connect();
        connect.subscribe(CoolRequestIdeaTopic.COOL_REQUEST_SETTING_CHANGE, (CoolRequestIdeaTopic.BaseListener) () -> loadText());
        loadText();
        Disposer.register(CoolRequestPluginDisposable.getInstance(project), connect);
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



