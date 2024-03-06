package com.cool.request.idea.listener;

import com.cool.request.common.state.SettingPersistentState;
import com.cool.request.common.state.SettingsState;
import com.intellij.ide.AppLifecycleListener;
import com.intellij.openapi.actionSystem.KeyboardShortcut;
import com.intellij.openapi.actionSystem.Shortcut;
import com.intellij.openapi.keymap.KeymapManager;

import javax.swing.*;


public class CoolRequestPluginApplicationLifecycleListener implements AppLifecycleListener {
    @Override
    public void appStarted() {
        SettingsState state = SettingPersistentState.getInstance().getState();

        KeyStroke keyStroke =KeyStroke.getKeyStroke(state.searchApiKeyCode, state.searchApiModifiers, false);
        SettingPersistentState.getInstance().setCurrentKeyStroke(keyStroke);
        Shortcut shortcut = new KeyboardShortcut(keyStroke, null);
        KeymapManager.getInstance().getActiveKeymap().addShortcut("com.cool.request.HotkeyAction", shortcut);
    }
}
