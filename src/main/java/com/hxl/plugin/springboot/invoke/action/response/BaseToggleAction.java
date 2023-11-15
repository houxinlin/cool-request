package com.hxl.plugin.springboot.invoke.action.response;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ToggleAction;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class BaseToggleAction extends ToggleAction {
    private final ToggleManager toggleManager;
    private final String text;
    public BaseToggleAction(String text, Icon icon, ToggleManager toggleManager) {
        super(text, text, icon);
        this.toggleManager = toggleManager;
        this.text = text;
    }
    @Override
    public boolean isSelected(@NotNull AnActionEvent e) {
        return toggleManager.isSelected(this.text);
    }

    @Override
    public void setSelected(@NotNull AnActionEvent e, boolean state) {
        toggleManager.setSelect(this.text);
    }
}
