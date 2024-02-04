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
