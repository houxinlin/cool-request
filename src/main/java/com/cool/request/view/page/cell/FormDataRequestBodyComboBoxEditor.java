/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * FormDataRequestBodyComboBoxEditor.java is part of Cool Request
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

import com.cool.request.common.constant.CoolRequestConfigConstant;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.event.ItemEvent;

public class FormDataRequestBodyComboBoxEditor extends DefaultCellEditor implements TableCellEditor {
    private JComboBox<String> comboBox;
    public FormDataRequestBodyComboBoxEditor(JTable jTable) {
        super(new JComboBox<>(new String[]{CoolRequestConfigConstant.Identifier.FILE, CoolRequestConfigConstant.Identifier.TEXT}));
        comboBox = (JComboBox<String>) getComponent();
        comboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                jTable.revalidate();
                jTable.invalidate();
            }
        });
        comboBox.setOpaque(true);
    }
    @Override
    public Object getCellEditorValue() {
        return comboBox.getSelectedItem();
    }
}
