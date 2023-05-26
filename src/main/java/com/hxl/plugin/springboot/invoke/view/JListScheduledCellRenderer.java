package com.hxl.plugin.springboot.invoke.view;


import com.hxl.plugin.springboot.invoke.bean.SpringBootScheduledEndpoint;

import javax.swing.*;
import java.awt.*;

public class JListScheduledCellRenderer extends JPanel implements ListCellRenderer<Object> {
    private static final int VERTICAL_SPACING = 8;
    private static final Color SELECTION_BACKGROUND = Color.decode("#595B5D");  // 设置选中项的背景色
    private static final Color SELECTION_FOREGROUND = Color.WHITE; // 设置选中项的前景色
    private JLabel text = new JLabel();


    public JListScheduledCellRenderer() {
        setLayout(new BorderLayout());
        setBorder(null);
        add(text, BorderLayout.CENTER);
        text.setOpaque(false);
        Dimension size = text.getPreferredSize();
        size.height = 30;
        text.setPreferredSize(size);
        setOpaque(true);
        setBorder(BorderFactory.createEmptyBorder(VERTICAL_SPACING, VERTICAL_SPACING, VERTICAL_SPACING, VERTICAL_SPACING));

    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        SpringBootScheduledEndpoint springBootScheduledEndpoint = (SpringBootScheduledEndpoint) value;
        text.setText(springBootScheduledEndpoint.getClassName() + "." + springBootScheduledEndpoint.getMethodName());
        if (isSelected) {
            setBackground(SELECTION_BACKGROUND);
            setForeground(SELECTION_FOREGROUND);
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }
        return this;
    }
}