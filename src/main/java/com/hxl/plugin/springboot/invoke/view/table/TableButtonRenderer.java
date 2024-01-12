package com.hxl.plugin.springboot.invoke.view.table;

import com.hxl.plugin.springboot.invoke.view.widget.PanelAction;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class TableButtonRenderer extends DefaultTableCellRenderer {
    private PanelAction panelAction = new PanelAction();

    public TableButtonRenderer(PanelAction action) {
        this.panelAction = action;
    }

    @Override
    public Component getTableCellRendererComponent(JTable jtable, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (isSelected){
            panelAction.setBackground(jtable.getSelectionBackground());
        }else {
            panelAction.setBackground(jtable.getBackground());
        }
        return panelAction;
    }

}
