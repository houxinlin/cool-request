package com.cool.request.action.actions;

import com.cool.request.common.bean.EmptyEnvironment;
import com.cool.request.common.bean.RequestEnvironment;
import com.cool.request.common.state.CoolRequestEnvironmentPersistentComponent;
import com.cool.request.common.constant.CoolRequestIdeaTopic;
import com.cool.request.common.constant.icons.CoolRequestIcons;
import com.cool.request.utils.StringUtils;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * 点击环境
 */
public class RequestEnvironmentAnAction extends BaseAnAction {
    public RequestEnvironmentAnAction(Project project) {
        super(project, () -> "Environment Setting", AllIcons.Scope.Production);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        DefaultActionGroup group = new DefaultActionGroup();
        group.add(new EnvironmentSettingAnAction(getProject()));
        group.addSeparator();
        //加载用户配置的环境
        CoolRequestEnvironmentPersistentComponent.State coolRequestEnvironmentPersistentComponentState
                = CoolRequestEnvironmentPersistentComponent.getInstance(getProject());
        for (RequestEnvironment environment : coolRequestEnvironmentPersistentComponentState.getEnvironments()) {
            boolean isSelect = StringUtils.isEqualsIgnoreCase(coolRequestEnvironmentPersistentComponentState.getSelectId(), environment.getId());
            group.add(new EnvironmentItemAnAction(getProject(), environment, isSelect ? CoolRequestIcons.GREEN : null));
        }
        //添加一个空环境
        EmptyEnvironment emptyEnvironment = new EmptyEnvironment();
        boolean isSelect = StringUtils.isEqualsIgnoreCase(coolRequestEnvironmentPersistentComponentState.getSelectId(), emptyEnvironment.getId());
        group.add(new EnvironmentItemAnAction(getProject(), emptyEnvironment, isSelect ? CoolRequestIcons.GREEN : null));


        DataContext dataContext = e.getDataContext();
        JBPopupFactory.getInstance().createActionGroupPopup(
                        null, group, dataContext, JBPopupFactory.ActionSelectionAid.SPEEDSEARCH,
                        false, null, 10, null, "popup@RequestEnvironmentAnAction")
                .showUnderneathOf(e.getInputEvent().getComponent());
    }

    /**
     * 环境item
     */
    public static class EnvironmentItemAnAction extends BaseAnAction {
        private RequestEnvironment requestEnvironment;

        public EnvironmentItemAnAction(Project project, RequestEnvironment requestEnvironment, Icon icon) {
            super(project, () -> requestEnvironment.getEnvironmentName(), icon);
            this.requestEnvironment = requestEnvironment;
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            //设置环境
            CoolRequestEnvironmentPersistentComponent.getInstance(getProject()).setSelectId(requestEnvironment.getId());
            getProject().getMessageBus().syncPublisher(CoolRequestIdeaTopic.ENVIRONMENT_CHANGE).event();
        }
    }
}
