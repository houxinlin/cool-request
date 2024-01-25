package com.cool.request.view.component;

import com.cool.request.view.ToolComponentPage;
import com.cool.request.view.page.BaseTablePanelWithToolbarPanelImpl;
import com.cool.request.view.page.cell.*;
import com.cool.request.view.table.TableCellAction;
import com.cool.request.view.widget.btn.toggle.ToggleAdapter;
import com.cool.request.view.widget.btn.toggle.ToggleButton;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.table.JBTable;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.EventObject;

/**
 * 静态资源服务器组件
 */
public class StaticResourceServerPage extends BaseTablePanelWithToolbarPanelImpl implements ToolComponentPage {
    public static final String PAGE_NAME = "StaticResourceServerPage";

    public StaticResourceServerPage(Project project) {
        super(project);

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
    protected void initDefaultTableModel(JBTable jTable, DefaultTableModel defaultTableModel) {
        jTable.getColumnModel().getColumn(0).setMaxWidth(90);

        jTable.getColumnModel().getColumn(0).setCellRenderer(new ToggleButtonRenderer());
        jTable.getColumnModel().getColumn(0).setCellEditor(new ToggleButtonEditor(jTable));

        jTable.getColumnModel().getColumn(1).setCellEditor(new TextFieldWithBrowseButtonEditable(getProject(), jTable));
        jTable.getColumnModel().getColumn(1).setCellRenderer(new TextFieldWithBrowseButtonRenderer());


        jTable.getColumnModel().getColumn(2).setCellEditor(new DefaultJTextCellEditable(getProject()));
        jTable.getColumnModel().getColumn(2).setCellRenderer(new DefaultJTextCellRenderer());

    }


    private static class ToggleButtonEditor implements TableCellEditor {
        private ToggleButton toggleButton;
        private JPanel root = new JPanel(new FlowLayout(FlowLayout.CENTER));

        public ToggleButtonEditor(JTable jTable) {
            toggleButton = new ToggleButton();
            toggleButton.addEventToggleSelected(new ToggleAdapter() {
                @Override
                public void onSelected(boolean selected) {
                    super.onSelected(selected);
                    System.out.println("onSelected" + selected);
                    jTable.setValueAt(selected, jTable.getEditingRow(), jTable.getEditingColumn());
                }
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
            toggleButton1.setSelected(Boolean.getBoolean(value.toString()));
            root.add(toggleButton1);
            return root;
        }
    }
}
