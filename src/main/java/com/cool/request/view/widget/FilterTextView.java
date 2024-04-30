/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * FilterTextView.java is part of Cool Request
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

import com.cool.request.common.icons.CoolRequestIcons;
import com.intellij.ui.JBColor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class FilterTextView extends JPanel {
    private final JLabel titleLabel = createLabel(new JBColor(Color.GRAY, Color.GRAY));
    private final JLabel contentLabel = createLabel(new JBColor(Color.BLACK, Color.WHITE));

    private JLabel createLabel(Color color) {
        JLabel jLabel = new JLabel();
        jLabel.setForeground(color);
        jLabel.setText("");
        return jLabel;
    }

    public FilterTextView(String title, ClickListener clickListener) {
        super(new FlowLayout(FlowLayout.LEFT));
        titleLabel.setText(title + ":");
        this.add(titleLabel);
        this.add(contentLabel);
        this.add(new JLabel(CoolRequestIcons.DOWN));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                clickListener.onClick(() -> FilterTextView.this);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                titleLabel.setForeground(contentLabel.getForeground());
                setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                titleLabel.setForeground(JBColor.GRAY);
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
    }

    public void setContent(String text) {
        contentLabel.setText(text);
    }

    public static interface ClickListener {

        public void onClick(ComponentEvent component);
    }

    public static interface ComponentEvent {
        public Component getComponent();
    }
}
