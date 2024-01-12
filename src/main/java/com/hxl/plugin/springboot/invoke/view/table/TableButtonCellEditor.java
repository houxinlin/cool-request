package com.hxl.plugin.springboot.invoke.view.table;

import com.hxl.plugin.springboot.invoke.view.widget.PanelAction;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.util.EventObject;

public class TableButtonCellEditor extends DefaultCellEditor implements TableCellEditor {
    private PanelAction panelAction = new PanelAction();

    public TableButtonCellEditor(PanelAction panelAction) {
        super(new JCheckBox());
        this.panelAction = panelAction;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        panelAction.setBackground(table.getSelectionBackground());
        panelAction.setRow(row);
        return panelAction;
    }



}
