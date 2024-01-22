package com.hxl.plugin.springboot.invoke.view.page.cell;

import com.intellij.openapi.project.Project;

import javax.swing.*;
import java.awt.*;
import java.util.EventObject;

public class DefaultJTextCellEditable extends DefaultCellEditor {
    private Project project;

    public DefaultJTextCellEditable(JTextField jTextField, Project project) {
        super(jTextField);
        this.project = project;
    }

    public DefaultJTextCellEditable(Project project) {
        this(new JTextField(), project);
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        Component tableCellEditorComponent = super.getTableCellEditorComponent(table, value, isSelected, row, column);
        if (isSelected) {
            tableCellEditorComponent.setBackground(table.getSelectionBackground());
        }
        return tableCellEditorComponent;
    }

    @Override
    public boolean shouldSelectCell(EventObject anEvent) {
        return true;
    }

    @Override
    public boolean isCellEditable(EventObject anEvent) {
        return true;
    }


}
