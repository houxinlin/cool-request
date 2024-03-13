package com.cool.request.action.actions;


import com.cool.request.common.icons.KotlinCoolRequestIcons;
import com.cool.request.utils.ResourceBundleUtils;
import com.cool.request.utils.WebBrowseUtils;
import com.cool.request.view.events.IToolBarViewEvents;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class HelpAction extends DynamicAnAction {
    public HelpAction(Project project, IToolBarViewEvents iViewEvents) {
        super(project, () -> ResourceBundleUtils.getString("help"),
                () -> ResourceBundleUtils.getString("help"), KotlinCoolRequestIcons.INSTANCE.getHELP());
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        WebBrowseUtils.browse("https://plugin.houxinlin.com");
    }
}
