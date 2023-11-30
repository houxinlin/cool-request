package com.hxl.plugin.springboot.invoke.view;

import com.hxl.plugin.springboot.invoke.net.KeyValue;
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
    private static final String[] TABLE_HEADER_NAME = {"Key", "Value", "Delete"};
    private final DefaultTableModel defaultTableModel = new DefaultTableModel(null, TABLE_HEADER_NAME);
    private JBTable jTable;
    public BasicTableParamJPanel() {
        init();
    }

    public void setTableData(List<KeyValue> headers) {
        if (headers==null) headers =new ArrayList<>();
        headers.add(new KeyValue("",""));
        defaultTableModel.setRowCount(0);
        for (KeyValue header : headers) {
            defaultTableModel.addRow(new String[]{header.getKey(), header.getValue(), "Delete"});
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
        defaultTableModel.addRow(new String[]{"", "", "Delete"});
        jTable = new JBTable(defaultTableModel) {
            public boolean isCellEditable(int row, int column) {
                return true;
            }
        };
        Action delete = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                JTable table = (JTable) e.getSource();
                int modelRow = Integer.parseInt(e.getActionCommand());
                ((DefaultTableModel) table.getModel()).removeRow(modelRow);
                if (table.getModel().getRowCount() == 0) defaultTableModel.addRow(new String[]{"", "", "Delete"});
            }
        };
        defaultTableModel.addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.UPDATE && e.getColumn() == 0 && defaultTableModel.getValueAt(defaultTableModel.getRowCount() - 1, 0).toString().length() != 0) {
                String[] strings = {"", "", "Delete"};
                defaultTableModel.addRow(strings);
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
