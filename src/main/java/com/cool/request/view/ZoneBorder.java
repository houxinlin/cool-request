/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * ZoneBorder.java is part of Cool Request
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

package com.cool.request.view;

import com.intellij.util.ui.JBUI;

import javax.swing.border.Border;
import java.awt.*;

public class ZoneBorder implements Border {
    private static final int WIDTH = 1;
    private final Color colorN;
    private final Color colorE;
    private final Color colorS;
    private final Color colorW;

    public ZoneBorder(Color colorN, Color colorE, Color colorS, Color colorW) {
        this.colorN=colorN;
        this.colorE=colorE;
        this.colorS=colorS;
        this.colorW=colorW;
    }

    public boolean isBorderOpaque() {
        return false;
    }

    public Insets getBorderInsets(Component c) {
        return JBUI.insets(WIDTH);
    }

    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Color old = g.getColor();
        if (colorN != null) {
            g.setColor(colorN);
            g.fillRect(x, y, width, WIDTH);
        }
        if (colorE != null) {
            g.setColor(colorE);
            g.fillRect(x+width-WIDTH, y, WIDTH, height);
        }
        if (colorS != null) {
            g.setColor(colorS);
            g.fillRect(x, y+height-WIDTH, width, WIDTH);
        }
        if (colorW != null) {
            g.setColor(colorW);
            g.fillRect(x, y, WIDTH, height);
        }
        g.setColor(old);
    }
}