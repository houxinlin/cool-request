/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * SendButton.java is part of Cool Request
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

import com.cool.request.common.icons.KotlinCoolRequestIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.actionSystem.impl.ActionButton;
import com.intellij.ui.AnimatedIcon;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class SendButton extends ActionButton {
    private final SendAnAction sendAnAction;
    private static final Icon loadIcon = new AnimatedIcon.Default();
    private boolean isLoading = false;
    private final Presentation presentation;

    private SendButton(@NotNull AnAction action, Presentation presentation, String place, @NotNull Dimension minimumSize) {
        super(action, presentation, place, minimumSize);
        this.sendAnAction = ((SendAnAction) action);
        this.presentation = presentation;
    }

    public boolean isLoading() {
        return isLoading;
    }

    private SendButton(AnAction action, Presentation presentation) {
        this(action, presentation, "send", new Dimension(32, 32));
    }

    public static SendButton newSendButton() {
        Presentation presentation = new Presentation();
        presentation.setIcon(KotlinCoolRequestIcons.INSTANCE.getSEND().invoke());
        presentation.setEnabledAndVisible(true);
        return new SendButton(new SendAnAction(null), presentation);
    }

    public void reset() {
        setLoadingStatus(false);
    }

    public void setLoadingStatus(boolean isLoading) {
        this.isLoading = isLoading;
        presentation.setIcon(isLoading ? loadIcon : KotlinCoolRequestIcons.INSTANCE.getSEND().invoke());
        repaint();
        invalidate();
    }

    public void addActionListener(ActionListener actionListener) {
        if (sendAnAction != null) {
            sendAnAction.actionListener = actionListener;
        }
    }

    public Presentation getButtonPresentation() {
        return presentation;
    }

    static class SendAnAction extends AnAction {
        private ActionListener actionListener;

        public SendAnAction(ActionListener actionListener) {
            this.actionListener = actionListener;
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            actionListener.actionPerformed(null);
        }
    }
}
