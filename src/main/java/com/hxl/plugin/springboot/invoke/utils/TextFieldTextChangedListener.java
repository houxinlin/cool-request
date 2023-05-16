package com.hxl.plugin.springboot.invoke.utils;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class TextFieldTextChangedListener  implements DocumentListener {
    @Override
    public void insertUpdate(DocumentEvent e) {
        textChanged();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        textChanged();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        textChanged();
    }

    protected void textChanged() {
    }
}
