package com.cool.request.view.page.cell;

import com.intellij.openapi.ui.TextFieldWithBrowseButton;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class TextFieldWithBrowseButtonRenderer implements TableCellRenderer {
    private TextFieldWithBrowseButton textFieldWithBrowseButton = new TextFieldWithBrowseButton();

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        textFieldWithBrowseButton.setText(value.toString());
        return textFieldWithBrowseButton;
    }
}