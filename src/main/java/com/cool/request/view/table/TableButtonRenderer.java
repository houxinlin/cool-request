/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * TableButtonRenderer.java is part of Cool Request
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

package com.cool.request.view.table;

import com.cool.request.view.widget.PanelAction;

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
