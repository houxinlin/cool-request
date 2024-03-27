/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * FormDataRequestBodyValueRenderer.java is part of Cool Request
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

import com.intellij.icons.AllIcons;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class FormDataRequestBodyValueRenderer extends JPanel implements TableCellRenderer {
    private final JTextField fileJTextField = new JTextField();
    private final JPanel fileSelectJPanel = new JPanel(new BorderLayout());


    public FormDataRequestBodyValueRenderer() {
        fileSelectJPanel.add(fileJTextField, BorderLayout.CENTER);
        JLabel fileSelectJLabel = new JLabel(AllIcons.General.OpenDisk);
        fileSelectJPanel.add(fileSelectJLabel, BorderLayout.EAST);

    }

    @Override
    public Component getTableCellRendererComponent(JTable table,
                                                   Object value,
                                                   boolean isSelected,
                                                   boolean hasFocus,
                                                   int row, int column) {
        if (table.getValueAt(row, 3).equals("text")) {
            JTextField jTextField = new JTextField(value.toString());
            jTextField.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
            return jTextField;
        } else {
            fileJTextField.setText(value.toString());
            fileJTextField.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
            return fileSelectJPanel;
        }
    }
}