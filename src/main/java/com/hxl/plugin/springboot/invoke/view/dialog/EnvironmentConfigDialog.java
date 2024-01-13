package com.hxl.plugin.springboot.invoke.view.dialog;

import com.hxl.plugin.springboot.invoke.IdeaTopic;
import com.hxl.plugin.springboot.invoke.bean.RequestEnvironment;
import com.hxl.plugin.springboot.invoke.state.CoolRequestEnvironmentPersistentComponent;
import com.hxl.plugin.springboot.invoke.utils.ResourceBundleUtils;
import com.hxl.plugin.springboot.invoke.utils.StringUtils;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;

import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.table.JBTable;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EnvironmentConfigDialog extends DialogWrapper {
    private final JBTable requestEnvironmentJBTable = new JBTable();
    private final Project project;
    private List<RequestEnvironment> requestEnvironments;
    private NonEditableTableModel tableModel = null;

    public EnvironmentConfigDialog(@Nullable Project project) {
        super(project);
        this.project = project;
        setTitle(ResourceBundleUtils.getString("environment.setting"));

        setSize(600, 300);
        init();
        requestEnvironmentJBTable.setRowHeight(30);
        loadEnvironmentTable();

    }

    private void loadEnvironmentTable() {
        tableModel = new NonEditableTableModel(
                new Object[][]{},
                new Object[]{ResourceBundleUtils.getString("environment.name"), ResourceBundleUtils.getString("host.address")}
        );
        requestEnvironments = getRequestEnvironment();

        for (RequestEnvironment environment : requestEnvironments) {
            tableModel.addRow(new Object[]{environment.getEnvironmentName(), environment.getPrefix()});
        }
        requestEnvironmentJBTable.setModel(tableModel);
    }

    private static class NonEditableTableModel extends DefaultTableModel {
        NonEditableTableModel(Object[][] data, Object[] columnNames) {
            super(data, columnNames);
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    }

    private List<RequestEnvironment> getRequestEnvironment() {
        CoolRequestEnvironmentPersistentComponent.State instance = CoolRequestEnvironmentPersistentComponent.getInstance();
        List<RequestEnvironment> environments = instance.environments;
        if (environments == null) environments = new ArrayList<>();
        return environments;
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return ToolbarDecorator.createDecorator(requestEnvironmentJBTable)
                .disableUpAction()
                .disableDownAction()
                .setAddAction(anActionButton -> {
                    RequestEnvironment requestEnvironment = new RequestEnvironment();
                    new RequestInfoConfigDialog(project, requestEnvironment).show();
                    if (StringUtils.isEmpty(requestEnvironment.getPrefix()) || StringUtils.isEmpty(requestEnvironment.getEnvironmentName())) {
                        return;
                    }
                    requestEnvironment.setId(UUID.randomUUID().toString());
                    CoolRequestEnvironmentPersistentComponent.State instance = CoolRequestEnvironmentPersistentComponent.getInstance();
                    instance.environments.add(requestEnvironment);
                    ApplicationManager.getApplication().getMessageBus().syncPublisher(IdeaTopic.ENVIRONMENT_ADDED).event();
                    loadEnvironmentTable();
                })
                .setRemoveAction(anActionButton -> {
                    int selectedRow = requestEnvironmentJBTable.getSelectedRow();
                    if (selectedRow >= 0) {
                        RequestEnvironment environment = requestEnvironments.get(selectedRow);
                        CoolRequestEnvironmentPersistentComponent.State instance = CoolRequestEnvironmentPersistentComponent.getInstance();
                        instance.environments.remove(environment);
                        ApplicationManager.getApplication().getMessageBus().syncPublisher(IdeaTopic.ENVIRONMENT_ADDED).event();
                        loadEnvironmentTable();
                    }
                })
                .setEditAction(anActionButton -> {
                    int selectedRow = requestEnvironmentJBTable.getSelectedRow();
                    if (selectedRow >= 0) {
                        RequestEnvironment environment = requestEnvironments.get(selectedRow);
                        new RequestInfoConfigDialog(project, environment).show();

                        DefaultTableModel model = (DefaultTableModel) requestEnvironmentJBTable.getModel();
                        model.setValueAt(environment.getEnvironmentName(), selectedRow, 0);
                        model.setValueAt(environment.getPrefix(), selectedRow, 1);
                        ApplicationManager.getApplication().getMessageBus().syncPublisher(IdeaTopic.ENVIRONMENT_CHANGE).event();
                    }

                })
                .createPanel();
    }
}
