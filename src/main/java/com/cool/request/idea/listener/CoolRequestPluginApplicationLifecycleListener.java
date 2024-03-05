package com.cool.request.idea.listener;

import com.intellij.ide.AppLifecycleListener;
import com.intellij.openapi.actionSystem.KeyboardShortcut;
import com.intellij.openapi.actionSystem.Shortcut;
import com.intellij.openapi.keymap.KeymapManager;

import javax.swing.*;

import static java.awt.event.ActionEvent.CTRL_MASK;
import static java.awt.event.ActionEvent.SHIFT_MASK;
import static java.awt.event.KeyEvent.VK_S;


public class CoolRequestPluginApplicationLifecycleListener implements AppLifecycleListener {
    @Override
    public void appStarted() {
        KeyStroke keyStroke =KeyStroke.getKeyStroke(VK_S, CTRL_MASK  | SHIFT_MASK, false);
        Shortcut shortcut = new KeyboardShortcut(keyStroke, null);
        KeymapManager.getInstance().getActiveKeymap().addShortcut("com.cool.request.HotkeyAction", shortcut);
    }
}
