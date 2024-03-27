/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * KeymapPanel.java is part of Cool Request
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

package com.cool.request.view.widget;

import com.cool.request.common.state.SettingPersistentState;
import com.cool.request.common.state.SettingsState;
import com.cool.request.utils.ResourceBundleUtils;
import com.intellij.openapi.keymap.KeymapManager;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public class KeymapPanel extends JPanel {
    private final JLabel tipLabel = new JLabel();
    private final KeymapTextField keymapTextField = new KeymapTextField(false);
    private KeyStroke newKeyStroke;

    public KeymapPanel() {
        super(new BorderLayout());
        this.add(keymapTextField, BorderLayout.CENTER);
        this.add(tipLabel, BorderLayout.SOUTH);
        SettingsState state = SettingPersistentState.getInstance().getState();
        KeyStroke oldKeyStroke = KeyStroke.getKeyStroke(state.searchApiKeyCode, state.searchApiModifiers, false);
        keymapTextField.setKeyStroke(oldKeyStroke);
        keymapTextField.addPropertyChangeListener("keyStroke", evt -> {
            Object newValue = evt.getNewValue();
            KeyStroke keyStroke = (KeyStroke) newValue;
            @NotNull String[] actionIds = KeymapManager.getInstance().getActiveKeymap().getActionIds(keyStroke);
            if (actionIds.length > 0) {
                tipLabel.setText(ResourceBundleUtils.getString("shortcut.conflict"));
                return;
            }
            newKeyStroke = keyStroke;
            tipLabel.setText(ResourceBundleUtils.getString("shortcut.success"));

        });
    }

    public KeyStroke getNewKeyStroke() {
        return newKeyStroke;
    }
}
