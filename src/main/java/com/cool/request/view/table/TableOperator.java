package com.cool.request.view.table;

import javax.swing.*;

public interface TableOperator {
    public void deleteSelectRow();

    public void stopEditor();

    public JTable getTable();

    public Object getValueAt(int row, int col);

    public void registerTableDataChangeEvent(int i, TableDataChange tableDataChange);
}
