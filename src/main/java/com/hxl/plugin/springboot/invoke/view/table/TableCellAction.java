package com.hxl.plugin.springboot.invoke.view.table;

import com.hxl.plugin.springboot.invoke.view.widget.ActionButton;
import com.hxl.plugin.springboot.invoke.view.widget.PanelAction;
import com.intellij.icons.AllIcons;
import icons.MyIcons;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.EventObject;

public class TableCellAction {
    public static class DeleteButton extends ActionButton {
        public DeleteButton(ActionListener actionListener) {
            setIcon(AllIcons.Actions.GC);
            if (actionListener != null) addActionListener(actionListener);
        }
    }

    public static PanelAction newDeletePanelPanel(ActionListener actionListener) {
        return new PanelAction(new DeleteButton(actionListener));
    }

    public static class TableDeleteButtonRenderer extends TableButtonRenderer {
        public TableDeleteButtonRenderer() {
            super(newDeletePanelPanel(null));
        }
    }

    public static class TableDeleteButtonCellEditor extends TableButtonCellEditor {
        public TableDeleteButtonCellEditor(ActionListener action) {
            super(newDeletePanelPanel(action));
        }
    }

}

