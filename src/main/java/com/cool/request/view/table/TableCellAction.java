package com.cool.request.view.table;

import com.cool.request.common.icons.CoolRequestIcons;
import com.cool.request.view.widget.ActionButton;
import com.cool.request.view.widget.PanelAction;
import com.intellij.icons.AllIcons;

import java.awt.event.ActionListener;

public class TableCellAction {
    public static class DeleteButton extends ActionButton {
        public DeleteButton(ActionListener actionListener) {
            setIcon(AllIcons.Actions.GC);
            if (actionListener != null) addActionListener(actionListener);
        }
    }

    public static class DirectoryButton extends ActionButton {
        public DirectoryButton(ActionListener actionListener) {
            setIcon(CoolRequestIcons.MenuOpen);
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

    public static class TableDeleteAndDirectoryButtonRenderer extends TableButtonRenderer {
        public TableDeleteAndDirectoryButtonRenderer() {
            super(newPanel(new DirectoryButton(null), new DeleteButton(null)));
        }
    }

    public static PanelAction newPanel(ActionButton... actionButton) {
        return new PanelAction(actionButton);
    }

    public static class TableDeleteButtonCellEditor extends TableButtonCellEditor {
        public TableDeleteButtonCellEditor(ActionListener action) {
            super(newDeletePanelPanel(action));
        }
    }

    public static class TableDeleteAndDirectoryButtonCellEditor extends TableButtonCellEditor {
        public TableDeleteAndDirectoryButtonCellEditor(ActionListener action) {
            super(newPanel(new DirectoryButton(action), new DeleteButton(action)));
        }
    }
}

