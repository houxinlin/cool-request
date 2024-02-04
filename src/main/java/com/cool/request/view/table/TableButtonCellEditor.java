package com.cool.request.view.table;

import com.cool.request.view.widget.PanelAction;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;

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
        panelAction.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
        return panelAction;
    }

}
