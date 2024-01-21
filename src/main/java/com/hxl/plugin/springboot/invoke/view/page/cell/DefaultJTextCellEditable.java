package com.hxl.plugin.springboot.invoke.view.page.cell;

import com.intellij.openapi.ui.JBPopupMenu;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.ListPopup;
import com.intellij.openapi.ui.popup.util.BaseListPopupStep;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

public class DefaultJTextCellEditable extends DefaultCellEditor {
    public DefaultJTextCellEditable() {
        super(new Header());
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        Component tableCellEditorComponent = super.getTableCellEditorComponent(table, value, isSelected, row, column);
        if (isSelected) {
            tableCellEditorComponent.setBackground(table.getSelectionBackground());
        }

        return tableCellEditorComponent;
    }

    @Override
    public boolean shouldSelectCell(EventObject anEvent) {
        return true;
    }

    @Override
    public boolean isCellEditable(EventObject anEvent) {
        return true;
    }

    static class Header extends JTextField {
        private ListPopup jbPopupMenu;

        private void showSugges() {
            List<String> a =new ArrayList<>();
            for (int i = 0; i < 50; i++) {
                a.add("a"+i);
            }
            JBPopupFactory.getInstance()
                    .createListPopup(new BaseListPopupStep("a",a))
                    .showInCenterOf(Header.this);

        }

        public Header() {

            getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    showSugges();
                }

                @Override
                public void removeUpdate(DocumentEvent e) {

                }

                @Override
                public void changedUpdate(DocumentEvent e) {

                }
            });
        }
    }
}
