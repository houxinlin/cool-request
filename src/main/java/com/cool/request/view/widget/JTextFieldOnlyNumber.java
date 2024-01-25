package com.cool.request.view.widget;

import com.intellij.ui.components.JBTextField;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class JTextFieldOnlyNumber extends JBTextField {
    public JTextFieldOnlyNumber() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c)) {
                    e.consume();
                }
            }
        });
    }
}
