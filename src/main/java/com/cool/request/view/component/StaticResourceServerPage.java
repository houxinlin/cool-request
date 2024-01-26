package com.cool.request.view.component;

import com.cool.request.component.http.StaticResourcePersistent;
import com.cool.request.component.http.StaticResourceServerService;
import com.cool.request.component.http.StaticServer;
import com.cool.request.view.ToolComponentPage;
import com.cool.request.view.page.BaseTablePanelWithToolbarPanelImpl;
import com.cool.request.view.page.cell.DefaultJTextCellEditable;
import com.cool.request.view.page.cell.DefaultJTextCellRenderer;
import com.cool.request.view.page.cell.TextFieldWithBrowseButtonEditable;
import com.cool.request.view.page.cell.TextFieldWithBrowseButtonRenderer;
import com.cool.request.view.widget.JTextFieldOnlyNumber;
import com.cool.request.view.widget.btn.toggle.ToggleAdapter;
import com.cool.request.view.widget.btn.toggle.ToggleButton;
import com.intellij.openapi.project.Project;
import com.intellij.ui.table.JBTable;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.EventObject;
import java.util.List;
import java.util.UUID;

/**
 * 静态资源服务器组件
 */
public class StaticResourceServerPage extends BaseTablePanelWithToolbarPanelImpl
        implements ToolComponentPage, CellEditorListener, TableModelListener {
    public static final String PAGE_NAME = "StaticResourceServerPage";

    public StaticResourceServerPage(Project project) {
        super(project, new ToolbarBuilder().all());
        List<StaticServer> staticServers = StaticResourcePersistent.getInstance().getStaticServers();
        for (StaticServer staticServer : staticServers) {
            addNewRow(new Object[]{false, staticServer.getRoot(), staticServer.getPort()});
        }
    }


    @Override
    public void addRow() {
        StaticServer staticServer = new StaticServer(UUID.randomUUID().toString(), 0, "");
        StaticResourcePersistent.getInstance().getStaticServers().add(staticServer);
        super.addRow();
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
        return new Object[]{false, "", 0};
    }

    @Override
    public void tableChanged(TableModelEvent e) {
        if (e.getType() == TableModelEvent.UPDATE) {
            Object source = e.getSource();
            List<StaticServer> staticServers = StaticResourcePersistent.getInstance().getStaticServers();
            if (getjTable().getEditingRow() < staticServers.size()) {
                String root = getDefaultTableModel().getValueAt(getjTable().getEditingRow(), 1).toString();
                int port = Integer.valueOf(getDefaultTableModel().getValueAt(getjTable().getEditingRow(), 2).toString());
                staticServers.get(getjTable().getEditingRow()).setRoot(root);
                staticServers.get(getjTable().getEditingRow()).setPort(port);
                System.out.println("更新");
            }
//            System.out.println("tableChanged" + e.getType() +"  "+getjTable().getEditingRow() +"  "+getjTable().getEditingRow() +" "+e.getColumn());
        }

    }

    @Override
    protected void initDefaultTableModel(JBTable jTable, DefaultTableModel defaultTableModel) {
        defaultTableModel.addTableModelListener(this);
        jTable.getColumnModel().getColumn(0).setMaxWidth(90);

        jTable.getColumnModel().getColumn(0).setCellRenderer(new ToggleButtonRenderer());
        jTable.getColumnModel().getColumn(0).setCellEditor(new ToggleButtonEditor(jTable));

        TextFieldWithBrowseButtonEditable textFieldWithBrowseButtonEditable = new TextFieldWithBrowseButtonEditable(getProject(), jTable);
        textFieldWithBrowseButtonEditable.addCellEditorListener(this);
        jTable.getColumnModel().getColumn(1).setCellEditor(textFieldWithBrowseButtonEditable);
        jTable.getColumnModel().getColumn(1).setCellRenderer(new TextFieldWithBrowseButtonRenderer());


        jTable.getColumnModel().getColumn(2).setCellEditor(new DefaultJTextCellEditable(new JTextFieldOnlyNumber(), getProject()));
        jTable.getColumnModel().getColumn(2).setCellRenderer(new DefaultJTextCellRenderer());

    }

    @Override
    public void editingStopped(ChangeEvent e) {
        System.out.println(getjTable().getEditingRow());
        System.out.println("editingStopped");
    }

    @Override
    public void editingCanceled(ChangeEvent e) {
        System.out.println(getjTable().getEditingRow());
        System.out.println("editingCanceled");
    }

    private class ToggleButtonEditor implements TableCellEditor {
        private final ToggleButton toggleButton;
        private final JPanel root = new JPanel(new FlowLayout(FlowLayout.CENTER));

        public ToggleButtonEditor(JTable jTable) {
            toggleButton = new ToggleButton();
            toggleButton.addEventToggleSelected(new ToggleAdapter() {
                @Override
                public void onSelected(boolean selected) {
                    super.onSelected(selected);
                    int editingRow = jTable.getEditingRow();
                    int editingColumn = jTable.getEditingColumn();
                    if (editingColumn == -1 || editingRow == -1) {
                        return;
                    }
                    jTable.setValueAt(selected, editingRow, editingColumn);
                }
            });
            toggleButton.setInterceptor((boolean selected) -> {
                if (selected) {
                    int editingRow = jTable.getEditingRow();
                    int editingColumn = jTable.getEditingColumn();
                    if (editingColumn == -1 || editingRow == -1) {
                        return false;
                    }
                    stopEditor();
                    Object valueAt = getDefaultTableModel().getValueAt(editingRow, editingColumn);
                    StaticServer staticServer = new StaticServer();
                    String portStr = getDefaultTableModel().getValueAt(editingRow, 2).toString();
                    staticServer.setPort(Integer.valueOf(portStr));
                    staticServer.setRoot(getDefaultTableModel().getValueAt(editingRow, 1).toString());
                    StaticResourceServerService.getInstance().start(staticServer);
                    return true;
                }
                return true;
            });
            root.add(toggleButton);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            toggleButton.setSelected(Boolean.getBoolean(value.toString()));
            return root;
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

        @Override
        public boolean stopCellEditing() {
            return true;
        }

        @Override
        public void cancelCellEditing() {

        }

        @Override
        public void addCellEditorListener(CellEditorListener l) {

        }

        @Override
        public void removeCellEditorListener(CellEditorListener l) {

        }
    }

    protected static class ToggleButtonRenderer implements TableCellRenderer {
        private JPanel root = new JPanel(new FlowLayout(FlowLayout.CENTER));

        public ToggleButtonRenderer() {
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            root.removeAll();
            ToggleButton toggleButton1 = new ToggleButton();
            toggleButton1.setSelected(Boolean.parseBoolean(value.toString()));
            root.add(toggleButton1);
            return root;
        }
    }
}
