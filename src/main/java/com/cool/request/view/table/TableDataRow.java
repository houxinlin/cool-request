package com.cool.request.view.table;

public class TableDataRow {
    public int rowIndex;
    public int colSize;
    private Object[] data;

    public TableDataRow(int rowIndex, int colSize, Object[] data) {
        this.rowIndex = rowIndex;
        this.colSize = colSize;
        this.data = data;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }

    public int getColSize() {
        return colSize;
    }

    public void setColSize(int colSize) {
        this.colSize = colSize;
    }

    public Object[] getData() {
        return data;
    }

    public void setData(Object[] data) {
        this.data = data;
    }
}
