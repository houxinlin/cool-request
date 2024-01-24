package com.cool.request.action.response;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.util.NlsActions;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public abstract class BaseAction extends AnAction {
    public BaseAction(@Nullable @NlsActions.ActionText String text, Icon icon) {
        super(text, text, icon);
    }
}
