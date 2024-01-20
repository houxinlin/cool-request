package com.hxl.plugin.springboot.invoke.tool;

import com.intellij.openapi.project.Project;
import icons.MyIcons;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public abstract class MainToolWindowsActionManager {
    private List<MainToolWindowsAction> actions = new ArrayList<>();
    private Project project;

    public MainToolWindowsActionManager(Project project) {
        this.project = project;
        init();
        actions.add(createMainToolWindowsAction("Setting", MyIcons.SETTING, () -> new JPanel()));
    }

    public Project getProject() {
        return project;
    }

    protected void init() {
    }

    public void registerAction(MainToolWindowsAction action) {
        if (action != null) actions.add(action);
    }

    public List<MainToolWindowsAction> getActions() {
        return actions;
    }

    protected MainToolWindowsAction createMainToolWindowsAction(String name, Icon icon, MainToolWindowsAction.ViewFactory viewFactory) {
        return new MainToolWindowsAction(name, icon, viewFactory);
    }

}
