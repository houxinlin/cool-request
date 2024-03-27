/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * ContactDialog.java is part of Cool Request
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

public class ContactDialog extends DialogWrapper {
    private final JPanel root = new JPanel(new BorderLayout());
    private Project project;

    public ContactDialog(@Nullable Project project) {
        super(project);
        this.project = project;
        setSize(400, 400);
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

    private static class LinkJPanel extends JPanel {
        public LinkJPanel(final String url) {
            super(new FlowLayout());
            setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            JButton jButton = new JButton("browse");
            jButton.addActionListener(e -> {
                WebBrowseUtils.browse(url);
            });
            add(jButton, BorderLayout.CENTER);
        }
    }


    @Override
    protected @Nullable JComponent createCenterPanel() {
        JBTabsImpl jbTabs = new JBTabsImpl(project);
        jbTabs.addTab(new TabInfo(new BaseQrCodeJPanel("/img/wechat.png")).setText("WeChat"));
        jbTabs.addTab(new TabInfo(new BaseQrCodeJPanel("/img/public.png")).setText("公众号"));
        jbTabs.addTab(new TabInfo(new BaseQrCodeJPanel("/img/qq.png")).setText("QQ"));
        root.add(jbTabs.getComponent(), BorderLayout.CENTER);
        return jbTabs;
    }
}
