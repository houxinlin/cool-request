package com.cool.request.view.component;


import com.cool.request.component.static_server.StaticResourcePersistent;
import com.cool.request.component.static_server.StaticResourceServerService;
import com.cool.request.component.static_server.StaticServer;
import com.cool.request.utils.ResourceBundleUtils;
import com.cool.request.utils.SocketUtils;
import com.cool.request.utils.StringUtils;
import com.cool.request.view.ToolComponentPage;
import com.cool.request.view.page.BaseTablePanelWithToolbarPanelImpl;
import com.cool.request.view.page.cell.TextFieldWithBrowseButtonEditable;
import com.cool.request.view.page.cell.TextFieldWithBrowseButtonRenderer;
import com.cool.request.view.widget.btn.toggle.ToggleAdapter;
import com.cool.request.view.widget.btn.toggle.ToggleButton;
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
import java.util.EventObject;
import java.util.List;
import java.util.UUID;

/**
 * 静态资源服务器组件
 */
public class StaticResourceServerPage extends BaseTablePanelWithToolbarPanelImpl
        implements ToolComponentPage, TableModelListener {
    public static final String PAGE_NAME = "StaticResourceServerPage";

    public StaticResourceServerPage(Project project) {
        super(project, new ToolbarBuilder().enabledAdd().enabledRemove().enabledCopyRow());
        List<StaticServer> staticServers = StaticResourcePersistent.getInstance().getStaticServers();
        for (StaticServer staticServer : staticServers) {
            addNewRow(new Object[]{false, staticServer.getRoot(), staticServer.getPort()});
        }
    }


    @Override
    public void addRow() {
        stopEditor();
        StaticServer staticServer = new StaticServer(UUID.randomUUID().toString(), 6060, "");
        StaticResourcePersistent.getInstance().getStaticServers().add(staticServer);
        super.addRow();
    }

    @Override
    public void removeRow() {
        stopEditor();
        int selectedRow = getTable().getSelectedRow();
        List<StaticServer> staticServers = StaticResourcePersistent.getInstance().getStaticServers();
        StaticServer staticServer = staticServers.get(selectedRow);
        if (StaticResourceServerService.getInstance().isRunning(staticServer)) {
            StaticResourceServerService.getInstance().stop(staticServer);
        }
        staticServers.remove(selectedRow);
        super.removeRow();
    }

    @Override
    public String getPageId() {
        return PAGE_NAME;
    }

    @Override
    public void setAttachData(Object object) {

    }

    @Override
    protected Object[] getTableHeader() {
        return new Object[]{"", "Path", "Port"};
    }

    @Override
    protected Object[] getNewRowData() {
        return new Object[]{false, "", 6060};
    }

    @Override
    public void copyRow() {
    }

    @Override
    public void tableChanged(TableModelEvent e) {
        if (e.getType() == TableModelEvent.UPDATE) {
            List<StaticServer> staticServers = StaticResourcePersistent.getInstance().getStaticServers();
            int selectedRow = getTable().getSelectedRow();
            if (selectedRow < staticServers.size()) {
                StaticServer staticServer = staticServers.get(selectedRow);
                String root = getDefaultTableModel().getValueAt(selectedRow, 1).toString();
                try {
                    int port = Integer.parseInt(getDefaultTableModel().getValueAt(selectedRow, 2).toString());
                    staticServer.setRoot(root);
                    staticServer.setPort(port);
                } catch (NumberFormatException ignored) {
                }
            }
        }

    }

    @Override
    protected void initDefaultTableModel(JBTable jTable, DefaultTableModel defaultTableModel) {
        defaultTableModel.addTableModelListener(this);
        jTable.getColumnModel().getColumn(0).setMaxWidth(90);
        jTable.getColumnModel().getColumn(2).setWidth(180);

        jTable.getColumnModel().getColumn(0).setCellRenderer(new ToggleButtonRenderer());
        jTable.getColumnModel().getColumn(0).setCellEditor(new ToggleButtonEditor(jTable));

        TextFieldWithBrowseButtonEditable textFieldWithBrowseButtonEditable = new TextFieldWithBrowseButtonEditable(getProject(), jTable);
        jTable.getColumnModel().getColumn(1).setCellEditor(textFieldWithBrowseButtonEditable);
        jTable.getColumnModel().getColumn(1).setCellRenderer(new TextFieldWithBrowseButtonRenderer());


        jTable.getColumnModel().getColumn(2).setCellEditor(new PortFieldEditor());
        jTable.getColumnModel().getColumn(2).setCellRenderer(new PortFieldRenderer());

    }

    private void startServer(int selectedRow) {
        try {
            StaticServer staticServer = StaticResourcePersistent.getInstance().getStaticServers().get(selectedRow);
            StaticResourceServerService.getInstance().start(staticServer);
        } catch (Exception e) {
            Messages.showErrorDialog(e.getMessage(), "Tip");
        }
    }

    private void stopServer(int selectedRow) {
        StaticServer staticServer = StaticResourcePersistent.getInstance().getStaticServers().get(selectedRow);
        StaticResourceServerService.getInstance().stop(staticServer);
    }

    /**
     * 拦截参数是否正常
     */
    private boolean doInterceptor(boolean selected) {
        int selectedRow = getTable().getSelectedRow();
        int selectedColumn = getTable().getSelectedColumn();

        if (selectedColumn == -1 || selectedRow == -1) {
            return false;
        }
        if (selected) {
            String path = getDefaultTableModel().getValueAt(selectedRow, 1).toString();
            String portStr = getDefaultTableModel().getValueAt(selectedRow, 2).toString();
            try {
                if (StringUtils.isEmpty(path) || !Files.isDirectory(Paths.get(path))) {
                    Messages.showErrorDialog(ResourceBundleUtils.getString("server.dir.not.exist.exist.err"), "Tip");
                    return false;
                }
                //一些字符会导致解析错误
            } catch (Exception e) {
                Messages.showErrorDialog(ResourceBundleUtils.getString("server.dir.not.exist.exist.err"), "Tip");
                return false;

            }
            int port = Integer.parseInt(portStr);
            if (port <= 0 || port > 65535) {
                Messages.showErrorDialog(ResourceBundleUtils.getString("port.overflow"), "Tip");
                return false;
            }
            boolean canConnection = SocketUtils.canConnection(port);
            if (canConnection) {
                Messages.showErrorDialog(ResourceBundleUtils.getString("port.bind.already"), "Tip");
                return false;
            }
        }
        return true;
    }

    private class ToggleButtonEditor implements TableCellEditor {
        protected EventListenerList listenerList = new EventListenerList();
        private final ToggleButton toggleButton;
        private final JPanel root = new JPanel(new FlowLayout(FlowLayout.CENTER));

        public ToggleButtonEditor(JTable jTable) {
            toggleButton = new ToggleButton();
            toggleButton.addEventToggleSelected(new ToggleAdapter() {
                @Override
                public void onSelected(boolean selected) {
                    super.onSelected(selected);
                    int row = jTable.getSelectedRow();
                    System.out.println("onSelected " + row + " " + selected);
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
            toggleButton.setInterceptor((boolean selected) -> {
                int selectedRow = getTable().getSelectedRow();
                if (selectedRow == -1) {
                    return false;
                }
//                return true;
                return doInterceptor(selected);
            });
            root.add(toggleButton);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            toggleButton.setSelected(Boolean.parseBoolean(value.toString()));
            System.out.println("getTableCellEditorComponent");
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
        private final JPanel root = new JPanel(new FlowLayout(FlowLayout.CENTER));
        private final ToggleButton toggleButton = new ToggleButton();

        public ToggleButtonRenderer() {

            root.add(toggleButton);

        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            toggleButton.setSelected(Boolean.parseBoolean(value.toString()));
            return root;
        }
    }

    public static class PortFieldEditor extends PortField implements TableCellEditor {
        protected EventListenerList listenerList = new EventListenerList();

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            setValue(value);
            return this;
        }

        @Override
        public Object getCellEditorValue() {
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
            return this;
        }
    }
}
