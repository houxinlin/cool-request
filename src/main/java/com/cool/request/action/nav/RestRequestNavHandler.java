/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * RestRequestNavHandler.java is part of Cool Request
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

package com.cool.request.action.nav;

import com.cool.request.common.service.ControllerMapService;
import com.cool.request.common.service.ProjectViewSingleton;
import com.cool.request.components.http.Controller;
import com.cool.request.components.http.StaticController;
import com.cool.request.components.http.net.HttpMethod;
import com.cool.request.lib.springmvc.config.reader.UserProjectContextPathReader;
import com.cool.request.lib.springmvc.config.reader.UserProjectServerPortReader;
import com.cool.request.lib.springmvc.utils.ParamUtils;
import com.cool.request.utils.*;
import com.cool.request.view.component.MainBottomHTTPContainer;
import com.cool.request.view.main.MainTopTreeView;
import com.cool.request.view.tool.ProviderManager;
import com.cool.request.view.tool.ToolActionPageSwitcher;
import com.intellij.codeInsight.daemon.GutterIconNavigationHandler;
import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.ui.awt.RelativePoint;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static com.cool.request.common.constant.CoolRequestConfigConstant.PLUGIN_ID;

/**
 * @author zhangpj
 * @date 2024/01/17
 */
public class RestRequestNavHandler implements GutterIconNavigationHandler<PsiElement> {
    public StaticController buildController(PsiMethod psiMethod) {
        try {
            StaticController result = new StaticController();
            List<HttpMethod> httpMethods = PsiUtils.getHttpMethod(psiMethod);
            result.setContextPath("");
            result.setHttpMethod(!httpMethods.isEmpty() ? httpMethods.get(0).toString() : HttpMethod.GET.toString());
            result.setMethodName(psiMethod.getName());
            result.setOwnerPsiMethod(Arrays.asList(psiMethod));
            result.setParamClassList(PsiUtils.getParamClassList(psiMethod));
            result.setSimpleClassName(psiMethod.getContainingClass().getQualifiedName());
            List<String> httpUrl = ParamUtils.getHttpUrl(psiMethod);

            Module module = ModuleUtil.findModuleForPsiElement(psiMethod);
            if (module != null) {
                result.setUrl(StringUtils.joinUrlPath(
                        new UserProjectContextPathReader(psiMethod.getProject(), module).read(),
                        httpUrl.isEmpty() ? "" : httpUrl.get(0)));
                result.setServerPort(new UserProjectServerPortReader(psiMethod.getProject(), module).read());
            } else {
                result.setUrl(httpUrl.isEmpty() ? "" : httpUrl.get(0));
                result.setServerPort(8080);
            }
            result.setId(UUID.randomUUID().toString());
            return result;

        } catch (Exception ignored) {
        }
        return null;
    }

    @Override
    public void navigate(MouseEvent e, PsiElement elt) {
        Project project = elt.getProject();
        PsiMethod method = PsiTreeUtil.getParentOfType(elt, PsiMethod.class);
        ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow(PLUGIN_ID);
        if (toolWindow != null) {
            toolWindow.show();
        }
        // 单击导航
        if (SwingUtilities.isLeftMouseButton(e)) {
            //用户点击接口中的方法，接口中的方法有很多实现，所以这里要弹窗
            ControllerMapService controllerMapService = ControllerMapService.getInstance(project);
            List<Controller> controllerByPsiMethod = controllerMapService.findControllerByPsiMethod(project, method);
            if (controllerByPsiMethod.size() > 1) {
                DefaultActionGroup defaultActionGroup = new DefaultActionGroup();

                for (Controller controller : controllerByPsiMethod) {
                    defaultActionGroup.add(new PsiMethodAnAction(controller));
                }
                DataContext dataContext = DataManager.getInstance().getDataContext(e.getComponent());
                JBPopupFactory.getInstance().createActionGroupPopup("Choose a URL", defaultActionGroup, dataContext, JBPopupFactory.ActionSelectionAid.SPEEDSEARCH,
                        false).show(new RelativePoint(e.getLocationOnScreen()));
                return;
            }
            if (controllerByPsiMethod.isEmpty()) {
                StaticController customController = buildController(method);
                if (customController == null) {
                    NotifyUtils.notification(project, "Unable to execute this request");
                    return;
                }
                controllerByPsiMethod.add(customController);
            }
            if (!controllerByPsiMethod.isEmpty()) {
                toDebug(controllerByPsiMethod.get(0), project);
            }

        }
    }

    private static void toDebug(Controller controller, Project project) {

        //HTTP请求界面选中
        ProviderManager.findAndConsumerProvider(ToolActionPageSwitcher.class, project, toolActionPageSwitcher -> {
            toolActionPageSwitcher.goToByName(MainBottomHTTPContainer.PAGE_NAME, controller);
        });
        ProjectViewSingleton.getInstance(project).createAndGetMainBottomHTTPContainer().setAttachData(controller);

        MainTopTreeView.RequestMappingNode requestMappingNodeByController = ControllerMapService.getInstance(project)
                .findRequestMappingNodeByController(project, controller);
        if (requestMappingNodeByController == null) return;
        //JTree中选择节点
        ProviderManager.findAndConsumerProvider(MainTopTreeView.class, project, mainTopTreeView -> {
            mainTopTreeView.selectNode(requestMappingNodeByController);
        });
    }

    static class PsiMethodAnAction extends AnAction {
        private final Controller controller;

        public PsiMethodAnAction(Controller controller) {
            super(controller::getUrl, HttpMethodIconUtils.getIconByHttpMethod(controller.getHttpMethod()));
            this.controller = controller;
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            NavigationUtils.jumpToControllerMethod(e.getProject(), controller);
            toDebug(controller, e.getProject());
        }
    }

}
