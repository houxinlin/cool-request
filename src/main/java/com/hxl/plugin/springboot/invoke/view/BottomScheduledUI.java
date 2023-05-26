package com.hxl.plugin.springboot.invoke.view;

import javax.swing.*;
import java.awt.*;

public class BottomScheduledUI extends JPanel {
    private JButton jButton;
    private InvokeClick invokeClick;


    public void setText(String text) {
        jButton.setText(text);
    }

    public BottomScheduledUI(InvokeClick invokeClick) {
        this.invokeClick = invokeClick;
        setLayout(new GridBagLayout());
        jButton = new JButton("");
        jButton.addActionListener(e -> invokeClick.onScheduledInvokeClick());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        add(jButton, gbc);
    }

    public interface InvokeClick {
        public void onScheduledInvokeClick();
    }
}
