/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * StaticResourceServerPage.java is part of Cool Request
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

package com.cool.request.view.component;


import com.cool.request.common.constant.CoolRequestIdeaTopic;
import com.cool.request.components.staticServer.*;
import com.cool.request.utils.*;
import com.cool.request.view.ToolComponentPage;
import com.cool.request.view.page.BaseTablePanelWithToolbarPanelImpl;
import com.cool.request.view.page.cell.TextFieldWithBrowseButtonEditable;
import com.cool.request.view.page.cell.TextFieldWithBrowseButtonRenderer;
import com.cool.request.view.table.TableCellAction;
import com.cool.request.view.widget.btn.toggle.ToggleAdapter;
import com.cool.request.view.widget.btn.toggle.ToggleButton;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.PortField;
import com.intellij.ui.table.JBTable;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.EventObject;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.cool.request.common.constant.CoolRequestConfigConstant.URL.STATIC_SERVER_HELP;

/**
 * 静态资源服务器组件
 */
public class StaticResourceServerPage extends BaseTablePanelWithToolbarPanelImpl
        implements ToolComponentPage, TableModelListener {
    public static final String PAGE_NAME = "File Web Server";
    private static final Object[] HEADER = new Object[]{"", "Path", "Port", "Action", "List Dir"};

    public StaticResourceServerPage(Project project) {
        super(project, new ToolbarBuilder()
                .enabledAdd()
                .enabledHelp(), DataModel.getInstance().getDefaultTableModel(HEADER));
        ApplicationManager.getApplication().
                getMessageBus()
                .connect()
                .subscribe(CoolRequestIdeaTopic.STATIC_SERVER_CHANGE, () -> {
                    defaultTableModel.fireTableDataChanged();
                });
    }

    @Override
    public void help() {
        WebBrowseUtils.browse(STATIC_SERVER_HELP);
    }

    @Override
    public void addRow() {
        stopEditor();
        StaticServer staticServer = new StaticServer(UUID.randomUUID().toString(), 6060, "");
        staticServer.setListDir(true);
        StaticResourcePersistent.getInstance().getStaticServers().add(staticServer);
        super.addRow();
        ApplicationManager.getApplication().getMessageBus().syncPublisher(CoolRequestIdeaTopic.STATIC_SERVER_CHANGE).event();
    }

    @Override
    public String getPageId() {
        return PAGE_NAME;
    }

    @Override
    public void attachViewData(Object object) {

    }

    @Override
    protected Object[] getTableHeader() {
        return HEADER;
    }

    @Override
    protected Object[] getNewNullRowData() {
        return new Object[]{false, "", 6060, "", true};
    }

    @Override
    public void tableChanged(TableModelEvent e) {
        if (e.getType() == TableModelEvent.UPDATE) {
            List<StaticServer> staticServers = StaticResourcePersistent.getInstance().getStaticServers();
            int selectedRow = getTable().getSelectedRow();
            if (selectedRow == -1) return;
            if (selectedRow < staticServers.size()) {
                StaticServer staticServer = staticServers.get(selectedRow);
                String root = getDefaultTableModel().getValueAt(selectedRow, 1).toString();
                try {
                    int port = Integer.parseInt(getDefaultTableModel().getValueAt(selectedRow, 2).toString());
                    staticServer.setRoot(root);
                    staticServer.setPort(port);
                    staticServer.setListDir(((Boolean) Optional.ofNullable(getDefaultTableModel().getValueAt(selectedRow, 4)).orElse(Boolean.FALSE)));

                    StaticResourceServerService service = ApplicationManager.getApplication().getService(StaticResourceServerService.class);

                    StaticResourceServer staticResourceServer = service.getStaticServerIfRunning(staticServer);
                    if (staticResourceServer != null) {
                        staticResourceServer.setListDir(staticServer.isListDir());
                    }
                } catch (NumberFormatException ignored) {
                }
            }
        }

    }

    @Override
    protected void initDefaultTableModel(JBTable jTable, DefaultTableModel defaultTableModel) {
        defaultTableModel.addTableModelListener(this);
        jTable.getColumnModel().getColumn(0).setMaxWidth(90);
        jTable.getColumnModel().getColumn(2).setMaxWidth(120);
        jTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        jTable.getColumnModel().getColumn(2).setWidth(120);

        jTable.getColumnModel().getColumn(3).setMaxWidth(120);
        jTable.getColumnModel().getColumn(3).setPreferredWidth(120);
        jTable.getColumnModel().getColumn(3).setWidth(120);

        jTable.getColumnModel().getColumn(4).setMaxWidth(60);
        jTable.getColumnModel().getColumn(4).setPreferredWidth(60);
        jTable.getColumnModel().getColumn(4).setWidth(60);

        jTable.getColumnModel().getColumn(0).setCellRenderer(new ToggleButtonRenderer());
        jTable.getColumnModel().getColumn(0).setCellEditor(new ToggleButtonEditor(jTable));

        jTable.getColumnModel().getColumn(1).setCellEditor(new TextFieldWithBrowseButtonEditable(getProject(), jTable));
        jTable.getColumnModel().getColumn(1).setCellRenderer(new TextFieldWithBrowseButtonRenderer());


        jTable.getColumnModel().getColumn(2).setCellEditor(new PortFieldEditor());
        jTable.getColumnModel().getColumn(2).setCellRenderer(new PortFieldRenderer());

        jTable.getColumnModel().getColumn(3).setCellEditor(new TableCellAction.StaticResourceServerPageActionsTableCellEditor(e -> {
            stopEditor();
            int selectedRow = getTable().getSelectedRow();
            if (selectedRow == -1) return;
            List<StaticServer> staticServers = StaticResourcePersistent.getInstance().getStaticServers();
            StaticServer staticServer = staticServers.get(selectedRow);

            if (e.getSource() instanceof TableCellAction.DirectoryButton) {
                BrowseUtils.openDirectory(staticServer.getRoot());
                return;
            }

            if (e.getSource() instanceof TableCellAction.WebBrowseButton) {
                WebBrowseUtils.browse("http://localhost:" + staticServer.getPort());
                return;
            }
            if (e.getSource() instanceof TableCellAction.DeleteButton) {
                StaticResourceServerService service = ApplicationManager.getApplication().getService(StaticResourceServerService.class);
                if (service.isRunning(staticServer)) {
                    service.stop(staticServer);
                }
                staticServers.remove(selectedRow);
                defaultTableModel.removeRow(selectedRow);
                defaultTableModel.fireTableDataChanged();
                ApplicationManager.getApplication().getMessageBus().syncPublisher(CoolRequestIdeaTopic.STATIC_SERVER_CHANGE).event();
            }

        }));
        jTable.getColumnModel().getColumn(3).setCellRenderer(new TableCellAction.StaticResourceServerPageActionsTableButtonRenderer());

        jTable.getColumnModel().getColumn(4).setCellEditor(jTable.getDefaultEditor(Boolean.class));
        jTable.getColumnModel().getColumn(4).setCellRenderer(jTable.getDefaultRenderer(Boolean.class));
        jTable.setRowHeight(35);

    }

    private void startServer(int selectedRow) {
        try {
            StaticServer staticServer = StaticResourcePersistent.getInstance()
                    .getStaticServers()
                    .get(selectedRow);
            StaticResourceServerService service = ApplicationManager.getApplication()
                    .getService(StaticResourceServerService.class);
            service.start(staticServer);
        } catch (Exception e) {
            Messages.showErrorDialog(e.getMessage(), ResourceBundleUtils.getString("tip"));
        } finally {
            ApplicationManager.getApplication().getMessageBus().syncPublisher(CoolRequestIdeaTopic.STATIC_SERVER_CHANGE).event();
        }
    }

    private void stopServer(int selectedRow) {
        try {
            StaticServer staticServer = StaticResourcePersistent.getInstance().getStaticServers().get(selectedRow);
            StaticResourceServerService service = ApplicationManager.getApplication().getService(StaticResourceServerService.class);
            service.stop(staticServer);
        } finally {
            ApplicationManager.getApplication().getMessageBus().syncPublisher(CoolRequestIdeaTopic.STATIC_SERVER_CHANGE).event();
        }
    }

    /**
     * 拦截参数是否正常
     */
    private boolean doInterceptor(boolean selected) {
        int selectedRow = getCurrentSelectRow();
        int selectedColumn = getCurrentSelectColumn();

        if (selectedColumn == -1 || selectedRow == -1) {
            return false;
        }
        if (selected) {
            String path = getDefaultTableModel().getValueAt(selectedRow, 1).toString();
            String portStr = getDefaultTableModel().getValueAt(selectedRow, 2).toString();
            try {
                if (StringUtils.isEmpty(path) || !Files.isDirectory(Paths.get(path))) {
                    Messages.showErrorDialog(ResourceBundleUtils.getString("server.dir.not.exist.exist.err"), ResourceBundleUtils.getString("tip"));
                    return false;
                }
                //一些字符会导致解析错误
            } catch (Exception e) {
                Messages.showErrorDialog(ResourceBundleUtils.getString("server.dir.not.exist.exist.err"), ResourceBundleUtils.getString("tip"));
                return false;

            }
            int port = Integer.parseInt(portStr);
            if (port <= 0 || port > 65535) {
                Messages.showErrorDialog(ResourceBundleUtils.getString("port.overflow"), ResourceBundleUtils.getString("tip"));
                return false;
            }
            boolean canConnection = SocketUtils.canConnection(port);
            if (canConnection) {
                Messages.showErrorDialog(ResourceBundleUtils.getString("port.bind.already"), ResourceBundleUtils.getString("tip"));
                return false;
            }
        }
        return true;
    }

    private class ToggleButtonEditor implements TableCellEditor {
        protected EventListenerList listenerList = new EventListenerList();
        private final ToggleButton toggleButton;
        private final JPanel root = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));

        public ToggleButtonEditor(JTable jTable) {
            toggleButton = new ToggleButton();
            toggleButton.addEventToggleSelected(new ToggleAdapter() {
                @Override
                public void onSelected(boolean selected) {
                    int row = getCurrentSelectRow();
                    if (row == -1) {
                        return;
                    }
                    if (selected) {
                        startServer(row);
                    } else {
                        stopServer(row);
                    }
                    getDefaultTableModel().setValueAt(selected, row, 0);
                }
            });
            toggleButton.setInterceptor((boolean selected) -> doInterceptor(selected));
            root.add(toggleButton);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            toggleButton.setSelected(Boolean.parseBoolean(value.toString()));
            root.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
            return root;
        }


        @Override
        public boolean stopCellEditing() {
            fireEditingStopped();
            return true;
        }

        @Override
        public void cancelCellEditing() {
            fireEditingStopped();
        }

        @Override
        public void addCellEditorListener(CellEditorListener l) {
            listenerList.add(CellEditorListener.class, l);
        }

        @Override
        public void removeCellEditorListener(CellEditorListener l) {
            listenerList.remove(CellEditorListener.class, l);
        }

        protected void fireEditingStopped() {
            Object[] listeners = listenerList.getListenerList();
            for (int i = listeners.length - 2; i >= 0; i -= 2) {
                if (listeners[i] == CellEditorListener.class) {

                    ((CellEditorListener) listeners[i + 1]).editingStopped(new ChangeEvent(this));
                }
            }
        }

        @Override
        public Object getCellEditorValue() {
            return toggleButton.isSelected();
        }

        @Override
        public boolean isCellEditable(EventObject anEvent) {
            return true;
        }

        @Override
        public boolean shouldSelectCell(EventObject anEvent) {
            return true;
        }
    }

    protected static class ToggleButtonRenderer implements TableCellRenderer {
        private final JPanel root = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        private final ToggleButton toggleButton = new ToggleButton();

        public ToggleButtonRenderer() {
            root.add(toggleButton);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            toggleButton.setSelected(Boolean.parseBoolean(value.toString()));
            root.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
            return root;
        }
    }

    public static class PortFieldEditor extends PortField implements TableCellEditor {
        protected EventListenerList listenerList = new EventListenerList();

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            setValue(value);
            this.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
            setBorder(null);
            setEnabled(!Boolean.parseBoolean(table.getValueAt(row, 0).toString()));
            return this;
        }

        @Override
        public Object getCellEditorValue() {
            try {
                this.commitEdit();
            } catch (ParseException e) {
            }
            return this.getValue();
        }

        @Override
        public boolean isCellEditable(EventObject anEvent) {
            return true;
        }

        @Override
        public boolean shouldSelectCell(EventObject anEvent) {
            return true;
        }

        @Override
        public boolean stopCellEditing() {
            fireEditingStopped();
            return false;
        }

        @Override
        public void cancelCellEditing() {
            fireEditingStopped();
        }

        protected void fireEditingStopped() {
            Object[] listeners = listenerList.getListenerList();
            for (int i = listeners.length - 2; i >= 0; i -= 2) {
                if (listeners[i] == CellEditorListener.class) {
                    ((CellEditorListener) listeners[i + 1]).editingStopped(new ChangeEvent(this));
                }
            }
        }

        @Override
        public void addCellEditorListener(CellEditorListener l) {
            listenerList.add(CellEditorListener.class, l);
        }

        @Override
        public void removeCellEditorListener(CellEditorListener l) {
            listenerList.remove(CellEditorListener.class, l);
        }
    }

    public static class PortFieldRenderer extends PortField implements TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setValue(value);
            this.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
            setBorder(null);
            setEnabled(!Boolean.parseBoolean(table.getValueAt(row, 0).toString()));
            return this;
        }
    }
}
