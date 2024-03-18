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
