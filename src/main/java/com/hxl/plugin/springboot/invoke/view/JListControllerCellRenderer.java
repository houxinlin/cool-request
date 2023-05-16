package com.hxl.plugin.springboot.invoke.view;

import javax.swing.*;
import javax.swing.text.Style;
import java.awt.*;

public class JListControllerCellRenderer extends JPanel implements ListCellRenderer<Object> {
    private final JLabel topLabel;
    private final JLabel bottomLabel;
    private final JLabel rightLabel;
    private static final int VERTICAL_SPACING = 8;
    private static final Color SELECTION_BACKGROUND = Color.decode("#595B5D");  // 设置选中项的背景色
    private static final Color SELECTION_FOREGROUND = Color.WHITE; // 设置选中项的前景色

    public JListControllerCellRenderer() {
        setLayout(new BorderLayout());
        setBorder(null);
        topLabel = new JLabel();
        bottomLabel = new JLabel();
        rightLabel = new JLabel();
        topLabel.setFont(new Font("黑体", Font.BOLD,15));
        topLabel.setOpaque(false);
        bottomLabel.setOpaque(false);
        rightLabel.setOpaque(false);

        JPanel leftPanel = new JPanel();
        leftPanel.setOpaque(false);
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.add(topLabel);
        leftPanel.add(bottomLabel);

        add(leftPanel, BorderLayout.WEST);
        add(rightLabel, BorderLayout.EAST);
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        // 根据需要自定义文本框的样式和数据绑定
        String[] data = (String[]) value;
        topLabel.setText(data[0]);
        bottomLabel.setText(data[1]);
        rightLabel.setText(data[2]);
        if (isSelected) {
            setBackground(SELECTION_BACKGROUND);
            setForeground(SELECTION_FOREGROUND);
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }
        setEnabled(list.isEnabled());
        setFont(list.getFont());
        setOpaque(true);
        setBorder(BorderFactory.createEmptyBorder(VERTICAL_SPACING, VERTICAL_SPACING, VERTICAL_SPACING, VERTICAL_SPACING));
        return this;
    }
}
