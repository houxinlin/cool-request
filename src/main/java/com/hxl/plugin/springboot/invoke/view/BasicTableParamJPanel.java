package com.hxl.plugin.springboot.invoke.view;

import com.hxl.plugin.springboot.invoke.net.KeyValue;
import com.hxl.plugin.springboot.invoke.utils.ResourceBundleUtils;
import com.intellij.ui.table.JBTable;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public abstract class BasicTableParamJPanel extends JPanel {
    private static final String[] TABLE_HEADER_NAME = {"Key", "Value", ResourceBundleUtils.getString("delete")};
    private static final String[] TABLE_HEADER_VALUE = {"", "", ResourceBundleUtils.getString("delete")};
    private final DefaultTableModel defaultTableModel = new DefaultTableModel(null, TABLE_HEADER_NAME);
    private JBTable jTable;

    public BasicTableParamJPanel() {
        init();
    }

    public void setTableData(List<KeyValue> headers) {
        if (headers == null) headers = new ArrayList<>();
        headers.add(new KeyValue("", ""));
        defaultTableModel.setRowCount(0);
        for (KeyValue header : headers) {
            defaultTableModel.addRow(new String[]{header.getKey(), header.getValue(), ResourceBundleUtils.getString("delete")});
        }
        jTable.revalidate();
    }

    public List<KeyValue> getTableMap() {
        List<KeyValue> result = new ArrayList<>();
        foreach((s, s2) -> {
            result.add(new KeyValue(s, s2));
        });
        return result;
    }

    public void foreach(BiConsumer<String, String> biConsumer) {
        for (int i = 0; i < jTable.getModel().getRowCount(); i++) {
            String key = jTable.getModel().getValueAt(i, 0).toString();
            String value = jTable.getModel().getValueAt(i, 1).toString();
            if (!("".equals(value) && "".equals(key))) {
                biConsumer.accept(key, value);
            }
        }
    }

    private void init() {
        setLayout(new BorderLayout());
        defaultTableModel.addRow(new String[]{"", "", ResourceBundleUtils.getString("delete")});
        jTable = new JBTable(defaultTableModel) {
            public boolean isCellEditable(int row, int column) {
                return true;
            }
        };
        Action delete = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                JTable table = (JTable) e.getSource();
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                int rowCount = model.getRowCount();
                int emptyValues = 0;
                for (int i = 0; i < rowCount; i++) {
                    if (model.getValueAt(i, 0).toString().isEmpty() && model.getValueAt(i, 1).toString().isEmpty()) {
                        emptyValues++;
                    }
                }
                int modelRow = Integer.parseInt(e.getActionCommand());
                //如果删除的是空行，并且空行数需要至少一个才能删除
                if ((model.getValueAt(modelRow, 0).toString().isEmpty() &&
                        model.getValueAt(modelRow, 1).toString().isEmpty() &&
                        emptyValues > 1) || (!model.getValueAt(modelRow, 0).toString().isEmpty() || !
                        model.getValueAt(modelRow, 1).toString().isEmpty())) {
                    ((DefaultTableModel) table.getModel()).removeRow(modelRow);
                }
                if (table.getModel().getRowCount() == 0) {
                    defaultTableModel.addRow(TABLE_HEADER_VALUE);
                }
            }
        };
        defaultTableModel.addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.UPDATE && e.getColumn() == 0 && !defaultTableModel.getValueAt(defaultTableModel.getRowCount() - 1, 0).toString().isEmpty()) {
                defaultTableModel.addRow(TABLE_HEADER_VALUE);
            }
        });
        ButtonColumn buttonColumn = new ButtonColumn(jTable, delete, 2);
        buttonColumn.setMnemonic(KeyEvent.VK_D);
        jTable.setSelectionBackground(Color.getColor("#00000000"));
        jTable.setDefaultRenderer(Object.class, new CustomTableCellRenderer());
        jTable.setDefaultEditor(Object.class, new CustomTableCellEditor());
        jTable.setRowHeight(40);
        add(new JScrollPane(jTable), BorderLayout.CENTER);
    }
}
