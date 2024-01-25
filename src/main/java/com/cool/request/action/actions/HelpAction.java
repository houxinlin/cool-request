package com.cool.request.action.actions;


import com.cool.request.icons.MyIcons;
import com.cool.request.utils.ResourceBundleUtils;
import com.cool.request.view.events.IToolBarViewEvents;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class HelpAction extends BaseAnAction {
    private final IToolBarViewEvents iViewEvents;

    public HelpAction(Project project, IToolBarViewEvents iViewEvents) {
        super(project, () -> ResourceBundleUtils.getString("help"),
                () -> ResourceBundleUtils.getString("help"), MyIcons.HELP);
        this.iViewEvents = iViewEvents;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        iViewEvents.pluginHelp();
    }
}
