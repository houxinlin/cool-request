/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * TextFieldWithBrowseButtonEditable.java is part of Cool Request
 *
 * License: GPL-3.0+
 *
 * Cool Request is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Cool Request is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Cool Request.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.cool.request.view.page.cell;

import com.cool.request.utils.file.FileChooseUtils;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;

import javax.swing.*;
import java.awt.*;
import java.util.EventObject;

public class TextFieldWithBrowseButtonEditable extends DefaultCellEditor {
    private final TextFieldWithBrowseButton textFieldWithBrowseButton = new TextFieldWithBrowseButton();

    public TextFieldWithBrowseButtonEditable(Project project, JTable jTable) {
        super(new JTextField());
        textFieldWithBrowseButton.addActionListener(e -> {
            String file = FileChooseUtils.chooseDirectory(project);
            if (file != null) {
                int editingRow = jTable.getEditingRow();
                jTable.setValueAt(file, editingRow, jTable.getEditingColumn());
                textFieldWithBrowseButton.setText(file);
            }
        });
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        textFieldWithBrowseButton.setText(value.toString());
        textFieldWithBrowseButton.setBackground(isSelected?table.getSelectionBackground():table.getBackground());
        textFieldWithBrowseButton.setEnabled(!Boolean.parseBoolean(table.getValueAt(row, 0).toString()));
        return textFieldWithBrowseButton;
    }

    @Override
    public Object getCellEditorValue() {
        return textFieldWithBrowseButton.getText();
    }

    @Override
    public boolean stopCellEditing() {
        fireEditingStopped();
        return true;
    }


    @Override
    public boolean isCellEditable(EventObject anEvent) {
        return true;
    }

    @Override
    public boolean shouldSelectCell(EventObject anEvent) {
        return true;
    }

}
