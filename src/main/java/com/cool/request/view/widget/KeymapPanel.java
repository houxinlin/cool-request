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
