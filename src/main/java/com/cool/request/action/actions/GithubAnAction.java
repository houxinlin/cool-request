package com.cool.request.action.actions;

import com.cool.request.utils.WebBrowseUtils;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class GithubAnAction extends AnAction {

    public GithubAnAction() {
        super(() -> "Github", AllIcons.Vcs.Vendors.Github);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        WebBrowseUtils.browse("https://github.com/houxinlin/cool-request");
    }
}
