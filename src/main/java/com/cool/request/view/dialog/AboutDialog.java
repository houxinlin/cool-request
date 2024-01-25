package com.cool.request.view.dialog;

import com.cool.request.Constant;
import com.cool.request.icons.MyIcons;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class AboutDialog extends DialogWrapper {
    private JPanel rootPanel;
    private JLabel version;
    private JLabel icon;

    @Override
    protected @Nullable JComponent createCenterPanel() {
        version.setText("v"+Constant.VERSION);
        icon.setIcon(MyIcons.MAIN);
        return rootPanel;
    }

    public AboutDialog(@Nullable Project project) {
        super(project);
        init();
    }
}
