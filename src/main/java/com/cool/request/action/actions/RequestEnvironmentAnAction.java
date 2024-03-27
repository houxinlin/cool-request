/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * RequestEnvironmentAnAction.java is part of Cool Request
 *
 * License: GPL-3.0+
 *
 * Cool Request is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Cool Request is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Cool Request.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.cool.request.action.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

/**
 * 点击环境
 */
public class RequestEnvironmentAnAction extends BaseAnAction {
    public RequestEnvironmentAnAction(Project project) {
        super(project, () -> "Environment Setting", AllIcons.Scope.Production);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
//        DefaultActionGroup group = new DefaultActionGroup();
//        group.add(new EnvironmentSettingAnAction(getProject()));
//        group.addSeparator();
//        //加载用户配置的环境
//        CoolRequestEnvironmentPersistentComponent.State coolRequestEnvironmentPersistentComponentState
//                = CoolRequestEnvironmentPersistentComponent.getInstance(getProject());
//        for (RequestEnvironment environment : coolRequestEnvironmentPersistentComponentState.getEnvironments()) {
//            boolean isSelect = StringUtils.isEqualsIgnoreCase(coolRequestEnvironmentPersistentComponentState.getSelectId(), environment.getId());
//            group.add(new EnvironmentItemAnAction(getProject(), environment, isSelect ? CoolRequestIcons.GREEN : null));
//        }
//        //添加一个空环境
//        EmptyEnvironment emptyEnvironment = new EmptyEnvironment();
//        boolean isSelect = StringUtils.isEqualsIgnoreCase(coolRequestEnvironmentPersistentComponentState.getSelectId(), emptyEnvironment.getId());
//        group.add(new EnvironmentItemAnAction(getProject(), emptyEnvironment, isSelect ? CoolRequestIcons.GREEN : null));
//
//
//        DataContext dataContext = e.getDataContext();
//        JBPopupFactory.getInstance().createActionGroupPopup(
//                        null, group, dataContext, JBPopupFactory.ActionSelectionAid.SPEEDSEARCH,
//                        false, null, 10, null, "popup@RequestEnvironmentAnAction")
//                .showUnderneathOf(e.getInputEvent().getComponent());
    }

    /**
     * 环境item
     */
//    public static class EnvironmentItemAnAction extends BaseAnAction {
//        private RequestEnvironment requestEnvironment;
//
//        public EnvironmentItemAnAction(Project project, RequestEnvironment requestEnvironment, Icon icon) {
//            super(project, () -> requestEnvironment.getEnvironmentName(), icon);
//            this.requestEnvironment = requestEnvironment;
//        }
//
//        @Override
//        public void actionPerformed(@NotNull AnActionEvent e) {
//            //设置环境
//            CoolRequestEnvironmentPersistentComponent.getInstance(getProject()).setSelectId(requestEnvironment.getId());
//            getProject().getMessageBus().syncPublisher(CoolRequestIdeaTopic.ENVIRONMENT_CHANGE).event();
//        }
//    }
}
