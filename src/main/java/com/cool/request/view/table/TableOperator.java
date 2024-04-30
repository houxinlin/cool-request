package com.cool.request.view.table;

import javax.swing.*;
import java.util.List;
import java.util.function.Function;

public interface TableOperator {
    public Object[] getSelectData();
    public void deleteSelectRow();

    public void stopEditor();

    public JTable getTable();

    public Object getValueAt(int row, int col);

    public void registerTableDataChangeEvent(int i, TableDataChange tableDataChange);

    public List<TableDataRow> listTableData(Function<TableDataRow,Boolean> filter);

    public void removeAllData();

    void addNewRow(Object[] newEmptyRow);

    void removeRow(int rowIndex);

}
