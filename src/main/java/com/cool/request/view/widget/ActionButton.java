/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * ActionButton.java is part of Cool Request
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

import com.cool.request.common.constant.CoolRequestConfigConstant;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;

public class ActionButton extends JButton {
    private boolean mousePress;

    public ActionButton() {
        setPreferredSize(new Dimension(23, 23));
        setContentAreaFilled(false);
        setBorder(JBUI.Borders.empty(1));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent me) {
                mousePress = true;
            }

            @Override
            public void mouseReleased(MouseEvent me) {
                mousePress = false;
            }
        });
    }

//    @Override
//    protected void paintComponent(Graphics grphcs) {
//        Graphics2D g2 = (Graphics2D) grphcs.create();
//        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//        int width = getWidth();
//        int height = getHeight();
//        int size = Math.min(width, height);
//        int x = (width - size) / 2;
//        int y = (height - size) / 2;
//        g2.setColor(CoolRequestConfigConstant.Colors.TABLE_SELECT_BACKGROUND);
////        if (mousePress) {
////
////        } else {
////            g2.setColor(new Color(199, 199, 199));
////        }
//
//        g2.fill(new Ellipse2D.Double(x, y, size, size));
//        g2.dispose();
//        super.paintComponent(grphcs);
//    }
}
