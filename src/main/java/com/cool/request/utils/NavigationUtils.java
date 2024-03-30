/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * NavigationUtils.java is part of Cool Request
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

package com.cool.request.utils;

import com.cool.request.common.constant.CoolRequestConfigConstant;
import com.cool.request.common.service.ControllerMapService;
import com.cool.request.components.http.Controller;
import com.cool.request.components.scheduled.BasicScheduled;
import com.cool.request.view.main.MainTopTreeView;
import com.cool.request.view.tool.ProviderManager;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PsiTreeUtil;

import java.util.List;

import static com.cool.request.common.constant.CoolRequestConfigConstant.PLUGIN_ID;
import static com.cool.request.utils.PsiUtils.*;

/**
 * 导航栏工具类
 *
 * @author zhangpj
 * @date 2024/01/17
 */
public class NavigationUtils {


    /**
     * This method queries the scheduled tasks for the clicked method.
     *
     * @param project       The current project.
     * @param clickedMethod The clicked method.
     * @param qualifiedName The qualified name of the clicked method.
     * @return True if the scheduled task was found, false otherwise.
     */
    public static boolean navigationScheduledInMainJTree(Project project,
                                                         PsiMethod clickedMethod,
                                                         String qualifiedName) {
        MainTopTreeView coolIdeaPluginWindowView = project.getUserData(CoolRequestConfigConstant.MainTopTreeViewKey);
        if (coolIdeaPluginWindowView == null) return false;

//        for (List<MainTopTreeView.BasicScheduledMethodNode> value : coolIdeaPluginWindowView.getScheduleMapNodeMap().values()) {
//            for (MainTopTreeView.BasicScheduledMethodNode springScheduledMethodNode : value) {
//                if (((MainTopTreeView.XxlJobMethodNode) springScheduledMethodNode).getData().getClassName().equals(qualifiedName) &&
//                        clickedMethod.getName().equals(springScheduledMethodNode.getData().getMethodName())) {
//                    project.getMessageBus()
//                            .syncPublisher(CoolRequestIdeaTopic.SCHEDULED_CHOOSE_EVENT)
//                            .onChooseEvent(springScheduledMethodNode.getData());
//                    coolIdeaPluginWindowView.selectNode(springScheduledMethodNode);
//                    return true;
//                }
//            }
//        }
        return false;
    }

    /**
     * This method queries the controller node for the clicked method.
     *
     * @param project   The current project.
     * @param psiMethod The clicked method.
     * @return True if the controller node was found, false otherwise.
     */
    public static boolean navigationControllerInMainJTree(Project project, PsiMethod psiMethod) {
        List<Controller> controllerByPsiMethod = ControllerMapService.getInstance(project).findControllerByPsiMethod(project, psiMethod);
        //这里可能有多个，因为PsiMethod可能是接口中的方法，可能有多个Controller实现，最好弹出菜单选择
        if (!controllerByPsiMethod.isEmpty()) {
            MainTopTreeView.RequestMappingNode requestMappingNodeByController = ControllerMapService.getInstance(project)
                    .findRequestMappingNodeByController(project, controllerByPsiMethod.get(0));
            if (requestMappingNodeByController == null) return false;

            ProviderManager.findAndConsumerProvider(MainTopTreeView.class, project, mainTopTreeView -> {
                mainTopTreeView.selectNode(requestMappingNodeByController);
            });
            return true;
        }
        return false;
    }

    /**
     * 根据不同方法跳转到窗口导航栏
     */
    public static void jumpToNavigation(Project project, PsiMethod clickedMethod) {
        ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow(PLUGIN_ID);
        if (toolWindow == null) {
            return;
        }
        if (!toolWindow.isActive()) {
            toolWindow.activate(null);
        }
        toolWindow.show();

        String qualifiedName = "";
        if (clickedMethod.getContainingClass() != null) {
            qualifiedName = clickedMethod.getContainingClass().getQualifiedName();
        }

        if (navigationControllerInMainJTree(project, clickedMethod)) {
            return;
        }
        if (navigationScheduledInMainJTree(project, clickedMethod, qualifiedName)) {
            return;
        }
        NotifyUtils.notification(project, ResourceBundleUtils.getString("method.not.fount"));
    }

    /**
     * This method finds the method clicked on in the editor.
     *
     * @param e The action event that occurred.
     * @return The method that was clicked on or null if no method was clicked on.
     */
    public static PsiMethod findClickedMethod(AnActionEvent e) {
        PsiFile psiFile = e.getData(LangDataKeys.PSI_FILE);
        if (psiFile == null) {
            return null;
        }
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        if (editor != null) {
            Caret caret = editor.getCaretModel().getPrimaryCaret();
            int offset = caret.getOffset();
            PsiElement elementAtCaret = psiFile.findElementAt(offset);
            if (elementAtCaret instanceof PsiMethod) {
                return (PsiMethod) elementAtCaret;
            } else {
                return PsiTreeUtil.getParentOfType(elementAtCaret, PsiMethod.class, false);
            }
        }
        return null;
    }

    /**
     * Controller->Code跳转
     */
    public static void jumpToControllerMethod(Project project, Controller controller) {
        //优先从PsiMethod归属跳转
        for (PsiMethod psiMethod : controller.getOwnerPsiMethod()) {
            if (psiMethod.getContainingClass() != null && !psiMethod.getContainingClass().isInterface()) {
                PsiUtils.methodNavigate(psiMethod);
                return;
            }
        }
        PsiClass psiClass = findClassByName(project, controller.getModuleName(), controller.getSimpleClassName());
        if (psiClass == null) {
            psiClass = findClassByName(project, controller.getSimpleClassName());
        }
        if (psiClass != null) {
            PsiMethod httpMethodMethodInClass = findHttpMethodInClass(psiClass, controller);
            if (httpMethodMethodInClass != null) PsiUtils.methodNavigate(httpMethodMethodInClass);
        }
    }

    public static void jumpToCode(Project project, String className, String method) {
        PsiClass targetPsiClass = findClassByName(project, className);
        if (targetPsiClass == null) return;
        List<PsiMethod> methodInClass = findMethodInClass(targetPsiClass, method);
        if (methodInClass.isEmpty()) return;
        methodInClass.get(0).navigate(true);
    }

    public static void jumpToSpringScheduledMethod(Project project, BasicScheduled springScheduled) {
        PsiClass psiClass = findClassByName(project, springScheduled.getModuleName(), springScheduled.getClassName());
        if (psiClass != null) {
            PsiMethod methodInClass = findMethodInClassOne(psiClass, springScheduled.getMethodName());
            if (methodInClass != null) PsiUtils.methodNavigate(methodInClass);
        }
    }
}
