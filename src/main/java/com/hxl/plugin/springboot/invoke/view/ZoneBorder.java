package com.hxl.plugin.springboot.invoke.view;

import javax.swing.border.Border;
import java.awt.*;

public class ZoneBorder implements Border {
    private static final int WIDTH = 1;
    private Color colorN, colorE, colorS, colorW;

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
        return new Insets(WIDTH,WIDTH,WIDTH,WIDTH);
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