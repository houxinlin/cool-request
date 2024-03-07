package com.cool.request.view;

import com.cool.request.component.http.net.KeyValue;
import com.cool.request.view.page.BaseTablePanelWithToolbarPanelImpl;
import com.cool.request.view.page.cell.DefaultJTextCellEditable;
import com.cool.request.view.page.cell.DefaultJTextCellRenderer;
import com.cool.request.view.table.TableCellAction;
import com.cool.request.view.widget.AutoCompleteJTextField;
import com.intellij.openapi.project.Project;
import com.intellij.ui.LanguageTextField;
import com.intellij.ui.TextFieldWithAutoCompletion;
import com.intellij.ui.scale.JBUIScale;
import com.intellij.ui.table.JBTable;

import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public abstract class BasicKeyValueTablePanelParamPanel extends BaseTablePanelWithToolbarPanelImpl {
    private TextFieldWithAutoCompletion keyAutoComplete;

    private TextFieldWithAutoCompletion valueAutoComplete;

    public BasicKeyValueTablePanelParamPanel(Project project) {
        super(project);
    }

    public BasicKeyValueTablePanelParamPanel(Project project, Window window) {
        super(project, window);
    }

    @Override
    protected Object[] getTableHeader() {
        return new Object[]{"", "Key", "Value", ""};
    }

    @Override
    protected Object[] getNewNullRowData() {
        return new Object[]{true, "", "", ""};
    }

    protected List<String> getKeySuggest() {
        return new ArrayList<>();
    }

    protected List<String> getValueSuggest(String key) {
        return new ArrayList<>();
    }

    @Override
    protected void initDefaultTableModel(JBTable jTable, DefaultTableModel defaultTableModel) {
        jTable.getColumnModel().getColumn(0).setCellEditor(jTable.getDefaultEditor(Boolean.class));
        jTable.getColumnModel().getColumn(0).setCellRenderer(jTable.getDefaultRenderer(Boolean.class));

        keyAutoComplete = TextFieldWithAutoCompletion.create(getProject(), getKeySuggest(), true, "");
        valueAutoComplete = TextFieldWithAutoCompletion.create(getProject(), new ArrayList<>(), true, "");
        jTable.getColumnModel().getColumn(1).setCellEditor(new DefaultJTextCellEditable(keyAutoComplete, getProject()));
        jTable.getColumnModel().getColumn(1).setCellRenderer(new DefaultJTextCellRenderer());

        jTable.getColumnModel().getColumn(2).setCellEditor(new DefaultJTextCellEditable(valueAutoComplete, getProject()));
        jTable.getColumnModel().getColumn(2).setCellRenderer(new DefaultJTextCellRenderer());

        jTable.getColumnModel().getColumn(0).setMaxWidth(30);
        jTable.getColumnModel().getColumn(3).setMaxWidth(80);

        jTable.getColumnModel().getColumn(3).setCellEditor(new TableCellAction.TableDeleteButtonCellEditor(e -> removeClickRow()));
        jTable.getColumnModel().getColumn(3).setCellRenderer(new TableCellAction.TableDeleteButtonRenderer());

//        jTable.setRowHeight(new LanguageTextField().getPreferredSize().height * 2 + JBUIScale.scale(1));;
        defaultTableModel.addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                int column = e.getColumn();
                if (column == 1) {
//                    valueAutoComplete.setSuggest(getValueSuggest(defaultTableModel.getValueAt(row, 1).toString()));
                }
            }
        });
    }

    @Override
    protected List<Integer> getSelectRow() {
        List<Integer> rows = new ArrayList<>();
        foreachTable((objects, row) -> {
            if (Boolean.valueOf(objects.get(0).toString())) {
                rows.add(row);
            }
        });
        return rows;
    }

    public void setTableData(List<KeyValue> headers, boolean addNewLine) {
        if (headers == null) headers = new ArrayList<>();
        if (addNewLine) {
            headers.add(new KeyValue("", ""));
        }
        removeAllRow();
        for (KeyValue header : headers) {
            addNewRow(new Object[]{true, header.getKey(), header.getValue(), ""});
        }
    }

    public void setTableData(List<KeyValue> headers) {
        setTableData(headers, false);
    }

    public List<KeyValue> getTableMap() {
        List<KeyValue> result = new ArrayList<>();
        foreach((s, s2) -> {
            result.add(new KeyValue(s, s2));
        });
        return result;
    }

    public void foreach(BiConsumer<String, String> biConsumer) {
        foreachTable((objects, integer) -> {
            if (Boolean.valueOf(objects.get(0).toString())) {
                String key = objects.get(1).toString();
                String value = objects.get(2).toString();
                if (!("".equals(key))) {
                    biConsumer.accept(key, value);
                }
            }
        });

    }

}
