package com.cool.request.action.actions;

import com.cool.request.common.bean.components.controller.Controller;
import com.cool.request.common.constant.CoolRequestConfigConstant;
import com.cool.request.common.constant.CoolRequestIdeaTopic;
import com.cool.request.common.icons.CoolRequestIcons;
import com.cool.request.component.ComponentType;
import com.cool.request.component.api.scans.SpringMvcControllerScan;
import com.cool.request.component.api.scans.SpringScheduledScan;
import com.cool.request.view.events.IToolBarViewEvents;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;


public class StaticRefreshAction extends AnAction {
    private final Project project;
    private final SpringMvcControllerScan springMvcControllerScan = new SpringMvcControllerScan();
    private final SpringScheduledScan springScheduledScan = new SpringScheduledScan();
    private final IToolBarViewEvents iViewEvents;

    public StaticRefreshAction(Project project, IToolBarViewEvents iViewEvents) {
        super("Static Refresh", "Static refresh", CoolRequestIcons.SCAN);
        this.project = project;
        this.iViewEvents = iViewEvents;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        //先删除所有数据
        project.getMessageBus().syncPublisher(CoolRequestIdeaTopic.DELETE_ALL_DATA).onDelete();
        ApplicationManager.getApplication().getMessageBus().syncPublisher(CoolRequestIdeaTopic.REFRESH_CUSTOM_FOLDER).event();
        ProgressManager.getInstance().run(new Task.Backgroundable(project, "Scan...") {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                ApplicationManager.getApplication().runReadAction(() -> {
                    List<Controller> staticControllerScanResult = springMvcControllerScan.scan(project);
                    assert project != null;
                    Objects.requireNonNull(project.getUserData(CoolRequestConfigConstant.UserProjectManagerKey)).addComponent(ComponentType.CONTROLLER, staticControllerScanResult);
                    Objects.requireNonNull(project.getUserData(CoolRequestConfigConstant.UserProjectManagerKey)).addComponent(ComponentType.SCHEDULE, springScheduledScan.scan(project));
                });
            }
        });
    }
}
