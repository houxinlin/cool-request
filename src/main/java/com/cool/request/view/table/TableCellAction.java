/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * TableCellAction.java is part of Cool Request
 *
 * License: GPL-3.0+
 *
 * Cool Request is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Cool Request is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Cool Request.  If not, see <http://www.gnu.org/licenses/>.
 */

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

    public static class WebBrowseButton extends ActionButton {
        public WebBrowseButton(ActionListener actionListener) {
            setIcon(AllIcons.General.Web);
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

    public static class StaticResourceServerPageActionsTableButtonRenderer extends TableButtonRenderer {
        public StaticResourceServerPageActionsTableButtonRenderer() {
            super(newPanel(new DirectoryButton(null), new DeleteButton(null), new WebBrowseButton(null)));
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

    public static class StaticResourceServerPageActionsTableCellEditor extends TableButtonCellEditor {
        public StaticResourceServerPageActionsTableCellEditor(ActionListener action) {
            super(newPanel(new DirectoryButton(action), new DeleteButton(action), new WebBrowseButton(action)));
        }
    }
}

