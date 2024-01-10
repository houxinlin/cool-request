package com.hxl.plugin.springboot.invoke.action.actions;

import com.hxl.plugin.springboot.invoke.Constant;
import com.hxl.plugin.springboot.invoke.IdeaTopic;
import com.hxl.plugin.springboot.invoke.bean.components.controller.Controller;
import com.hxl.plugin.springboot.invoke.scans.controller.SpringMvcControllerScan;
import com.hxl.plugin.springboot.invoke.scans.scheduled.SpringScheduledScan;
import com.hxl.plugin.springboot.invoke.view.events.IToolBarViewEvents;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.List;


public class StaticRefreshAction extends AnAction {
    private Project project;
    private final SpringMvcControllerScan springMvcControllerScan = new SpringMvcControllerScan();
    private final SpringScheduledScan springScheduledScan =new SpringScheduledScan();
    private final IToolBarViewEvents iViewEvents;

    public StaticRefreshAction(Project project, IToolBarViewEvents iViewEvents) {
        super("Static Refresh");
        this.project = project;
        this.iViewEvents = iViewEvents;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        iViewEvents.clearAllData();
        ProgressManager.getInstance().run(new Task.Backgroundable(project, "Scan...") {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                while (project.getMessageBus().hasUndeliveredEvents(IdeaTopic.DELETE_ALL_DATA)){
                }
                ApplicationManager.getApplication().runReadAction(() -> {
                    List<Controller> staticControllerScanResult = springMvcControllerScan.scan(project);
                    assert project != null;
                    project.getUserData(Constant.UserProjectManagerKey).addControllerInfo(staticControllerScanResult);
                    project.getUserData(Constant.UserProjectManagerKey).addScheduledInfo(  springScheduledScan.scan(project));
                });
            }
        });
    }
}
