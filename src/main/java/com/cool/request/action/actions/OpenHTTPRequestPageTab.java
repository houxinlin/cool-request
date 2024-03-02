package com.cool.request.action.actions;

import com.cool.request.common.icons.CoolRequestIcons;
import com.cool.request.utils.ResourceBundleUtils;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class OpenHTTPRequestPageTab extends BaseAnAction {
    public OpenHTTPRequestPageTab(Project project) {
        super(project, () -> ResourceBundleUtils.getString("open.http.request.new.tab"), CoolRequestIcons.MAIN);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

    }
}
