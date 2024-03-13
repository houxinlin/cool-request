package com.cool.request.action.actions;

import com.cool.request.common.icons.KotlinCoolRequestIcons;
import com.cool.request.utils.ResourceBundleUtils;
import com.cool.request.view.events.IToolBarViewEvents;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;


public class RefreshAction extends DynamicAnAction {
    private final IToolBarViewEvents iViewEvents;

    public RefreshAction(Project project, IToolBarViewEvents iViewEvents) {
        super(project, () -> ResourceBundleUtils.getString("refresh"),
                () -> ResourceBundleUtils.getString("refresh"),
                () -> KotlinCoolRequestIcons.INSTANCE.getREFRESH().invoke());
        this.iViewEvents = iViewEvents;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        if (!iViewEvents.canRefresh()) return;
        new StaticRefreshAction(getProject()).actionPerformed(e);
//        Project project = e.getProject();
//
//        DefaultActionGroup defaultActionGroup = new DefaultActionGroup(new StaticRefreshAction(project),
//                new DynamicRefreshAction(project));
//        defaultActionGroup.getTemplatePresentation().setIcon(ADD_WITH_DROPDOWN);
//        defaultActionGroup.getTemplatePresentation().setText(ResourceBundleUtils.getString("refresh"));
//        defaultActionGroup.registerCustomShortcutSet(CommonShortcuts.getNewForDialogs(), null);
//
//
//        JBPopupFactory.getInstance().createActionGroupPopup(
//                        null, defaultActionGroup, e.getDataContext(), JBPopupFactory.ActionSelectionAid.SPEEDSEARCH,
//                        false, null, 10, null, "popup@RefreshAction")
//                .showUnderneathOf(e.getInputEvent().getComponent());

    }
}
