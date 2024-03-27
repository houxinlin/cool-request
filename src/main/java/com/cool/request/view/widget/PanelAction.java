/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * PanelAction.java is part of Cool Request
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

package com.cool.request.view.widget;

import java.awt.*;


public class PanelAction extends javax.swing.JPanel {
    private ActionButton[] actionButton;

    public PanelAction(ActionButton... actionButton) {
        initComponents(actionButton);
    }

    private void initComponents(ActionButton... actionButton) {
        this.setLayout(new FlowLayout());
        this.actionButton = actionButton;
        for (ActionButton button : actionButton) {
            add(button);
        }
    }

    public void setRow(int row) {

    }
}
