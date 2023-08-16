package com.hxl.plugin.springboot.invoke.view.page;

import com.hxl.plugin.springboot.invoke.view.ButtonColumn;
import com.hxl.plugin.springboot.invoke.view.page.cell.FormDataRequestBodyComboBoxEditor;
import com.hxl.plugin.springboot.invoke.view.page.cell.FormDataRequestBodyComboBoxRenderer;
import com.hxl.plugin.springboot.invoke.view.page.cell.FormDataRequestBodyValueEditor;
import com.hxl.plugin.springboot.invoke.view.page.cell.FormDataRequestBodyValueRenderer;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class FormDataRequestBodyPage extends JPanel {
    private static final String[] TABLE_HEADER_NAME = {"Key", "Value", "类型","操作"};

    private final DefaultTableModel defaultTableModel = new DefaultTableModel(null, TABLE_HEADER_NAME);
    private JTable jTable;

    public FormDataRequestBodyPage() {
        init();
    }

    private void init() {
        setLayout(new BorderLayout());
        defaultTableModel.addRow(new String[]{"","","text","Delete"});
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

                if (table.getModel().getRowCount()==0) defaultTableModel.addRow(new String[]{"", "","text", "Delete"});
            }
        };
        defaultTableModel.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                if (e.getType()==TableModelEvent.UPDATE &&  e.getColumn()==0 && defaultTableModel.getValueAt(defaultTableModel.getRowCount()-1, 0).toString().length()!=0){
                    String[] strings = {"", "","text", "Delete"};
                    defaultTableModel.addRow(strings);
                }
            }
        });
        ButtonColumn buttonColumn = new ButtonColumn(jTable, delete, 3);
        buttonColumn.setMnemonic(KeyEvent.VK_D);
        jTable.setSelectionBackground(Color.getColor("#00000000"));
        TableColumn column = jTable.getColumnModel().getColumn(2);
        column.setCellRenderer(new FormDataRequestBodyComboBoxRenderer(jTable));
        column.setCellEditor(new FormDataRequestBodyComboBoxEditor(jTable));


        TableColumn column1 = jTable.getColumnModel().getColumn(1);
        column1.setCellRenderer(new FormDataRequestBodyValueRenderer());
        column1.setCellEditor(new FormDataRequestBodyValueEditor(jTable));

        jTable.setRowHeight(35);

        jTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = jTable.rowAtPoint(evt.getPoint());
                int col = jTable.columnAtPoint(evt.getPoint());
                //如果点击的是文件
                if (col==1 && defaultTableModel.getValueAt(row,2).equals("file")){
                    JFileChooser fileChooser = new JFileChooser();
                    int returnValue = fileChooser.showOpenDialog(jTable);
                    if (returnValue == JFileChooser.APPROVE_OPTION) {
                        defaultTableModel.setValueAt(fileChooser.getSelectedFile().getAbsolutePath(),row,col);
                    }
                }
            }
        });
        add(new JScrollPane(jTable),BorderLayout.CENTER);
    }
}