/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * KeymapTextField.java is part of Cool Request
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

import com.intellij.icons.AllIcons;
import com.intellij.ide.IdeBundle;
import com.intellij.openapi.keymap.KeymapUtil;
import com.intellij.openapi.ui.JBPopupMenu;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.util.registry.Registry;
import com.intellij.ui.KeyStrokeAdapter;
import com.intellij.ui.components.fields.ExtendableTextField;
import com.intellij.util.ui.accessibility.ScreenReader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public final class KeymapTextField extends ExtendableTextField {
    private KeyStroke myKeyStroke;
    private int myLastPressedKeyCode = KeyEvent.VK_UNDEFINED;

   public KeymapTextField(boolean isFocusTraversalKeysEnabled) {
        enableEvents(AWTEvent.KEY_EVENT_MASK);
        setFocusTraversalKeysEnabled(isFocusTraversalKeysEnabled);
        if (isFocusTraversalKeysEnabled) {
            setExtensions(Extension.create(AllIcons.General.InlineAdd, AllIcons.General.InlineAddHover, getPopupTooltip(), this::showPopup));
        }
        setCaret(new DefaultCaret() {
            @Override
            public boolean isVisible() {
                return false;
            }
        });
    }

    private static boolean absolutelyUnknownKey(KeyEvent e) {
        return e.getKeyCode() == 0
                && e.getKeyChar() == KeyEvent.CHAR_UNDEFINED
                && e.getKeyLocation() == KeyEvent.KEY_LOCATION_UNKNOWN
                && e.getExtendedKeyCode() == 0;
    }

    @Override
    protected void processKeyEvent(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (getFocusTraversalKeysEnabled() && e.getModifiers() == 0 && e.getModifiersEx() == 0) {
            if (keyCode == KeyEvent.VK_ESCAPE || (keyCode == KeyEvent.VK_ENTER && myKeyStroke != null)) {
                super.processKeyEvent(e);
                return;
            }
        }

        final boolean isNotModifierKey = keyCode != KeyEvent.VK_SHIFT &&
                keyCode != KeyEvent.VK_ALT &&
                keyCode != KeyEvent.VK_CONTROL &&
                keyCode != KeyEvent.VK_ALT_GRAPH &&
                keyCode != KeyEvent.VK_META &&
                !absolutelyUnknownKey(e);

        if (isNotModifierKey) {
            // NOTE: when user presses 'Alt + Right' at Linux the IDE can receive next sequence KeyEvents: ALT_PRESSED -> RIGHT_RELEASED ->  ALT_RELEASED
            // RIGHT_PRESSED can be skipped, it depends on WM
            if (
                    e.getID() == KeyEvent.KEY_PRESSED
                            || (e.getID() == KeyEvent.KEY_RELEASED &&
                            SystemInfo.isLinux && (e.isAltDown() || e.isAltGraphDown()) && myLastPressedKeyCode != keyCode) // press-event was skipped
            ) {
                setKeyStroke(KeyStrokeAdapter.getDefaultKeyStroke(e));
            }

            if (e.getID() == KeyEvent.KEY_PRESSED)
                myLastPressedKeyCode = keyCode;
        }

        // Ensure TAB/Shift-TAB work as focus traversal keys, otherwise
        // there is no proper way to move the focus outside the text field.
        if (!getFocusTraversalKeysEnabled() && ScreenReader.isActive()) {
            setFocusTraversalKeysEnabled(true);
            try {
                KeyboardFocusManager.getCurrentKeyboardFocusManager().processKeyEvent(this, e);
            } finally {
                setFocusTraversalKeysEnabled(false);
            }
        }
    }

    void setKeyStroke(KeyStroke keyStroke) {
        KeyStroke old = myKeyStroke;
        if (old != null || keyStroke != null) {
            super.setText(KeymapUtil.getKeystrokeText(keyStroke));
            setCaretPosition(0);
            firePropertyChange("keyStroke", old, keyStroke);
        }
    }


    KeyStroke getKeyStroke() {
        return myKeyStroke;
    }

    @Override
    public void enableInputMethods(boolean enable) {
        super.enableInputMethods(enable && Registry.is("ide.settings.keymap.input.method.enabled"));
    }

    @Override
    public void setText(String text) {
        super.setText(text);
        setCaretPosition(0);
        if (text == null || text.isEmpty()) {
            myKeyStroke = null;
            firePropertyChange("keyStroke", null, null);
        }
    }

    private void showPopup() {
        JBPopupMenu menu = new JBPopupMenu();
        getKeyStrokes().forEach(stroke -> menu.add(getPopupAction(stroke)));
        Insets insets = getInsets();
        menu.show(this, getWidth() - insets.right, insets.top);
    }

    @NotNull
    private Action getPopupAction(@NotNull KeyStroke stroke) {
        return new AbstractAction(IdeBundle.message("button.set.0", KeymapUtil.getKeystrokeText(stroke))) {
            @Override
            public void actionPerformed(ActionEvent event) {
                setKeyStroke(stroke);
            }
        };
    }

    @NotNull
    private @NlsContexts.Tooltip String getPopupTooltip() {
        StringBuilder sb = new StringBuilder();
        String prefix = "";
        for (KeyStroke stroke : getKeyStrokes()) {
            if (0 == stroke.getModifiers()) {
                sb.append(prefix).append(KeymapUtil.getKeystrokeText(stroke));
                prefix = ", ";
            }
        }
        return IdeBundle.message("tooltip.text.add.shortcut.with.special.keys", sb.toString());
    }

    @NotNull
    private Iterable<KeyStroke> getKeyStrokes() {
        ArrayList<KeyStroke> list = new ArrayList<>();
        addKeyStrokes(list, getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS));
        addKeyStrokes(list, getFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS));
        addKeyStrokes(list, getFocusTraversalKeys(KeyboardFocusManager.UP_CYCLE_TRAVERSAL_KEYS));
        addKeyStrokes(list, getFocusTraversalKeys(KeyboardFocusManager.DOWN_CYCLE_TRAVERSAL_KEYS));

        return list;
    }

    private static void addKeyStrokes(@NotNull ArrayList<? super KeyStroke> list, @Nullable Iterable<? extends AWTKeyStroke> strokes) {
        if (strokes != null) {
            for (AWTKeyStroke stroke : strokes) {
                int keyCode = stroke.getKeyCode();
                if (keyCode != KeyEvent.VK_UNDEFINED) {
                    list.add(stroke instanceof KeyStroke
                            ? (KeyStroke) stroke
                            : KeyStroke.getKeyStroke(keyCode, stroke.getModifiers(), stroke.isOnKeyRelease()));
                }
            }
        }
    }
}