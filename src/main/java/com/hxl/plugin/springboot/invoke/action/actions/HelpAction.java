package com.hxl.plugin.springboot.invoke.action.actions;


import com.hxl.plugin.springboot.invoke.utils.ResourceBundleUtils;
import com.hxl.plugin.springboot.invoke.view.events.IToolBarViewEvents;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class HelpAction extends BaseAnAction {
    private final IToolBarViewEvents iViewEvents;

    public HelpAction(Project project, IToolBarViewEvents iViewEvents) {
        super(project, () -> ResourceBundleUtils.getString("help"), () -> ResourceBundleUtils.getString("help"), AllIcons.Actions.Help);
        this.iViewEvents = iViewEvents;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        iViewEvents.pluginHelp();
    }
}
