package com.cool.request.action.actions;

import com.cool.request.common.constant.CoolRequestIdeaTopic;
import com.cool.request.common.icons.CoolRequestIcons;
import com.cool.request.utils.NavigationUtils;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicBoolean;


public class StaticRefreshAction extends AnAction {
    private final Project project;
    private final AtomicBoolean refreshAtomicBoolean = new AtomicBoolean();

    public StaticRefreshAction(Project project) {
        super("Static Refresh", "Static refresh", CoolRequestIcons.SCAN);
        this.project = project;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        //先删除所有数据
        if (refreshAtomicBoolean.get()) return;
        project.getMessageBus().syncPublisher(CoolRequestIdeaTopic.DELETE_ALL_DATA).onDelete();
        ApplicationManager.getApplication().getMessageBus().syncPublisher(CoolRequestIdeaTopic.REFRESH_CUSTOM_FOLDER).event();
        refreshAtomicBoolean.set(true);
        NavigationUtils.staticRefreshView(project, () -> refreshAtomicBoolean.set(false));
    }
}
