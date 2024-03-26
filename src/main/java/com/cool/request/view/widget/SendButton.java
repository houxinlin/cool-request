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
