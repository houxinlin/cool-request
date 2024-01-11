package com.hxl.plugin.springboot.invoke.action.actions;

import com.hxl.plugin.springboot.invoke.utils.WebBrowseUtils;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class DynamicUrlAnAction extends AnAction {
    private final String url;

    /**
     * DynamicUrlAnAction is a class that extends AnAction.
     * @param title The text to be displayed as a tooltip for the component, when the component is visible.
     * @param icon The icon to display in the component.
     * @param url The url to open.
     */
    public DynamicUrlAnAction(String title, Icon icon, String url) {
        super(() -> title, icon);
        this.url = url;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        WebBrowseUtils.browse(this.url);
    }
}
