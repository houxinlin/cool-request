package com.cool.request.view.dialog;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class IpSelectionDialog extends DialogWrapper {
    private final List<String> ipAddresses;
    private final ComboBox<String> comboBox;

    public IpSelectionDialog(Project project, List<String> ipAddresses) {
        super(project);
        this.ipAddresses = ipAddresses;
        this.comboBox = new ComboBox<>(ipAddresses.toArray(new String[0]));
        init();
        setTitle("Select IP Address");
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel("Select an IP address:"), BorderLayout.NORTH);
        panel.add(comboBox, BorderLayout.CENTER);
        return panel;
    }

    @Nullable
    @Override
    protected ValidationInfo doValidate() {
        // Add any custom validation logic here if needed
        return super.doValidate();
    }

    @Nullable
    @Override
    protected JComponent createSouthPanel() {
        return super.createSouthPanel();
    }

    @Override
    protected void doOKAction() {
        // Handle OK action if needed
        super.doOKAction();
    }

    public String getSelectedIpAddress() {
        return comboBox.getSelectedItem() != null ? comboBox.getSelectedItem().toString() : null;
    }
}
