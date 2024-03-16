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
                clickListener.onClick(FilterTextView.this);
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

        public void onClick(Component component);
    }
}
