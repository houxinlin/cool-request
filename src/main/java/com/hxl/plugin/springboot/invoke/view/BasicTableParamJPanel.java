package com.hxl.plugin.springboot.invoke.view;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public abstract class BasicTableParamJPanel extends JPanel {
    private static final String[] TABLE_HEADER_NAME = {"Key", "Value", "操作"};

    private DefaultTableModel defaultTableModel = new DefaultTableModel(null, TABLE_HEADER_NAME);
    private JTable jTable;

    public BasicTableParamJPanel() {
        init();
    }

    private void init() {
        setLayout(new BorderLayout());
        defaultTableModel.addRow(new String[]{"","","Delete"});
        jTable = new JTable(defaultTableModel) {
            public boolean isCellEditable(int row, int column) {
                return true;
            }
        };
        Action delete = new AbstractAction()
        {
            public void actionPerformed(ActionEvent e)
            {
                JTable table = (JTable)e.getSource();
                int modelRow = Integer.valueOf( e.getActionCommand() );
                ((DefaultTableModel)table.getModel()).removeRow(modelRow);

                if (table.getModel().getRowCount()==0) defaultTableModel.addRow(new String[]{"", "", "Delete"});
            }
        };
        defaultTableModel.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                if (e.getType()==TableModelEvent.UPDATE &&  e.getColumn()==0 && defaultTableModel.getValueAt(defaultTableModel.getRowCount()-1, 0).toString().length()!=0){
                    String[] strings = {"", "", "Delete"};
                    defaultTableModel.addRow(strings);
                }
            }
        });
        ButtonColumn buttonColumn = new ButtonColumn(jTable, delete, 2);
        buttonColumn.setMnemonic(KeyEvent.VK_D);
        jTable.setSelectionBackground(Color.getColor("#00000000"));
        jTable.setDefaultRenderer(Object.class, new CustomTableCellRenderer());
        jTable.setDefaultEditor(Object.class, new CustomTableCellEditor());
        jTable.setRowHeight(35);
        add(new JScrollPane(jTable),BorderLayout.CENTER);
    }
}
