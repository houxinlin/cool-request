package com.cool.request.action.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.actionSystem.Toggleable;
import com.intellij.openapi.util.NlsActions;
import com.intellij.ui.AnActionButton;
import kotlin.jvm.functions.Function0;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.function.Supplier;

public abstract class DynamicIconToggleActionButton extends AnActionButton implements Toggleable {
    private Function0<Icon> icon;

    public DynamicIconToggleActionButton(@NlsActions.ActionText String text, Function0<Icon> icon) {
        super(() -> text, Presentation.NULL_STRING, icon.invoke());
        this.icon = icon;
    }

    public DynamicIconToggleActionButton(@NotNull Supplier<String> text, Icon icon) {
        super(text, Presentation.NULL_STRING, icon);
    }

    /**
     * Returns the selected (checked, pressed) state of the action.
     *
     * @param e the action event representing the place and context in which the selected state is queried.
     * @return true if the action is selected, false otherwise
     */
    public abstract boolean isSelected(AnActionEvent e);

    /**
     * Sets the selected state of the action to the specified value.
     *
     * @param e     the action event which caused the state change.
     * @param state the new selected state of the action.
     */
    public abstract void setSelected(AnActionEvent e, boolean state);

    @Override
    public final void actionPerformed(@NotNull AnActionEvent e) {
        final boolean state = !isSelected(e);
        setSelected(e, state);
        final Presentation presentation = e.getPresentation();
        Toggleable.setSelected(presentation, state);
    }

    @Override
    public void updateButton(@NotNull AnActionEvent e) {
        final boolean selected = isSelected(e);
        final Presentation presentation = e.getPresentation();
        presentation.setIcon(icon.invoke());
        Toggleable.setSelected(presentation, selected);

    }
}
