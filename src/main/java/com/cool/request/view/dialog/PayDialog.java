/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * PayDialog.java is part of Cool Request
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

package com.cool.request.view.dialog;

import com.cool.request.utils.WebBrowseUtils;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.tabs.TabInfo;
import com.intellij.ui.tabs.impl.JBTabsImpl;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class PayDialog extends DialogWrapper {
    private final JPanel root = new JPanel(new BorderLayout());
    private Project project;

    public PayDialog(@Nullable Project project) {
        super(project);
        this.project = project;
        setTitle("喜欢这个软件吗?打个赏吧!");
        setSize(440, 500);
        init();

    }

    private static class BaseQrCodeJPanel extends JPanel {
        public BaseQrCodeJPanel(String path) {
            super(new BorderLayout());
            ImageIcon imageIcon = new ImageIcon(getClass().getResource(path));

            setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            add(new JLabel(new ImageIcon(imageIcon.getImage().getScaledInstance(300, 300, Image.SCALE_SMOOTH))));
        }
    }


    @Override
    protected @Nullable JComponent createCenterPanel() {
        JBTabsImpl jbTabs = new JBTabsImpl(project);
        jbTabs.addTab(new TabInfo(new BaseQrCodeJPanel("/img/weixinpay.png")).setText("WeChat"));
        jbTabs.addTab(new TabInfo(new BaseQrCodeJPanel("/img/alipay.png")).setText("支付宝"));
        root.add(jbTabs.getComponent(), BorderLayout.CENTER);
        return jbTabs;
    }
}
