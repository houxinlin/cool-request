/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * DefaultJTextCellEditable.java is part of Cool Request
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

import com.cool.request.view.widget.AutocompleteField;

import javax.swing.*;
import java.awt.*;
import java.util.EventObject;

public class DefaultJTextCellEditable extends DefaultCellEditor {
    private final AutocompleteField textFieldWithAutoCompletion;

    public DefaultJTextCellEditable(AutocompleteField jTextField) {
        super(jTextField);
        this.textFieldWithAutoCompletion = jTextField;
    }

    public DefaultJTextCellEditable() {
        this(new AutocompleteField(null));
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        Component tableCellEditorComponent = super.getTableCellEditorComponent(table, value, isSelected, row, column);
        tableCellEditorComponent.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
        textFieldWithAutoCompletion.setText(value == null ? "" : value.toString());
        return textFieldWithAutoCompletion;
    }

    @Override
    public Object getCellEditorValue() {
        return textFieldWithAutoCompletion.getText();
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
