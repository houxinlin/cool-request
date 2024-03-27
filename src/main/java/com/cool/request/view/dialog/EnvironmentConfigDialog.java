/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * EnvironmentConfigDialog.java is part of Cool Request
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

import com.cool.request.common.bean.RequestEnvironment;
import com.cool.request.common.constant.CoolRequestIdeaTopic;
import com.cool.request.common.state.CoolRequestEnvironmentPersistentComponent;
import com.cool.request.utils.ResourceBundleUtils;
import com.cool.request.utils.StringUtils;
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
import java.util.stream.Collectors;

public class EnvironmentConfigDialog extends DialogWrapper {
    private final JBTable requestEnvironmentJBTable = new JBTable();
    private final Project project;
    private List<RequestEnvironment> requestEnvironmentsWithMerge = new ArrayList<>();
    private NonEditableTableModel tableModel = null;
    private List<RequestEnvironment> newAddRequestEnvironmentCache = new ArrayList<>();

    private List<RequestEnvironment> deleteRequestEnvironmentCache = new ArrayList<>();

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
        requestEnvironmentsWithMerge.clear();
        requestEnvironmentsWithMerge.addAll(getRequestEnvironment());

        for (RequestEnvironment environment : requestEnvironmentsWithMerge) {
            tableModel.addRow(new Object[]{environment.getEnvironmentName(), environment.getHostAddress()});
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
        List<RequestEnvironment> result = new ArrayList<>();

        CoolRequestEnvironmentPersistentComponent.State instance = CoolRequestEnvironmentPersistentComponent.getInstance(project);
        List<RequestEnvironment> environments = instance.getEnvironments();
        //从本地获取配置后，进行克隆，放置被直接修改，只有点ok的时候才进行修改
        for (RequestEnvironment environment : environments) {
            result.add(environment.clone());
        }
        result.addAll(newAddRequestEnvironmentCache);

        return result.stream().filter(requestEnvironment -> {
            for (RequestEnvironment deleteEnv : deleteRequestEnvironmentCache) {
                if (StringUtils.isEqualsIgnoreCase(deleteEnv.getId(), requestEnvironment.getId())) {
                    return false;
                }
            }
            return true;
        }).collect(Collectors.toList());
    }

    @Override
    protected void doOKAction() {
        super.doOKAction();
        CoolRequestEnvironmentPersistentComponent.State instance = CoolRequestEnvironmentPersistentComponent.getInstance(project);
        //刷新缓存数据到本地
        instance.getEnvironments().clear();
        for (RequestEnvironment requestEnvironment : requestEnvironmentsWithMerge) {
            instance.getEnvironments().add(requestEnvironment);
        }
        project.getMessageBus().syncPublisher(CoolRequestIdeaTopic.ENVIRONMENT_CHANGE).event();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return ToolbarDecorator.createDecorator(requestEnvironmentJBTable)
                .disableUpAction()
                .disableDownAction()
                .setAddAction(anActionButton -> {
                    RequestEnvironment requestEnvironment = new RequestEnvironment();
                    RequestEnvironmentInfoConfigDialog.showDialog(project, requestEnvironment);
                    if (StringUtils.isEmpty(requestEnvironment.getHostAddress()) || StringUtils.isEmpty(requestEnvironment.getEnvironmentName())) {
                        return;
                    }
                    requestEnvironment.setId(UUID.randomUUID().toString());
                    newAddRequestEnvironmentCache.add(requestEnvironment);
                    loadEnvironmentTable();
                })
                .setRemoveAction(anActionButton -> {
                    int selectedRow = requestEnvironmentJBTable.getSelectedRow();
                    if (selectedRow >= 0) {
                        RequestEnvironment environment = requestEnvironmentsWithMerge.get(selectedRow);
                        requestEnvironmentsWithMerge.remove(environment);
                        newAddRequestEnvironmentCache.remove(environment);
                        deleteRequestEnvironmentCache.add(environment);
                        loadEnvironmentTable();
                    }
                })
                .setEditAction(anActionButton -> {
                    int selectedRow = requestEnvironmentJBTable.getSelectedRow();
                    if (selectedRow >= 0) {
                        RequestEnvironment environment = requestEnvironmentsWithMerge.get(selectedRow);
                        RequestEnvironmentInfoConfigDialog.showDialog(project, environment);
                        DefaultTableModel model = (DefaultTableModel) requestEnvironmentJBTable.getModel();

                        model.setValueAt(environment.getEnvironmentName(), selectedRow, 0);
                        model.setValueAt(environment.getHostAddress(), selectedRow, 1);
                    }

                })
                .createPanel();
    }
}
