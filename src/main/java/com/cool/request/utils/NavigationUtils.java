package com.cool.request.utils;

import com.cool.request.common.bean.components.controller.Controller;
import com.cool.request.common.bean.components.scheduled.SpringScheduled;
import com.cool.request.common.constant.CoolRequestConfigConstant;
import com.cool.request.common.constant.CoolRequestIdeaTopic;
import com.cool.request.common.service.ControllerMapService;
import com.cool.request.component.api.scans.SpringMvcControllerScan;
import com.cool.request.component.api.scans.SpringScheduledScan;
import com.cool.request.view.main.MainTopTreeView;
import com.cool.request.view.tool.ProviderManager;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.application.ApplicationManager;
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
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

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

        for (List<MainTopTreeView.ScheduledMethodNode> value : coolIdeaPluginWindowView.getScheduleMapNodeMap().values()) {
            for (MainTopTreeView.ScheduledMethodNode scheduledMethodNode : value) {
                if (scheduledMethodNode.getData().getClassName().equals(qualifiedName) &&
                        clickedMethod.getName().equals(scheduledMethodNode.getData().getMethodName())) {
                    project.getMessageBus()
                            .syncPublisher(CoolRequestIdeaTopic.SCHEDULED_CHOOSE_EVENT)
                            .onChooseEvent(scheduledMethodNode.getData());
                    coolIdeaPluginWindowView.selectNode(scheduledMethodNode);
                    return true;
                }
            }
        }
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
     *
     * @param project
     * @param clickedMethod
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

        if (NavigationUtils.navigationControllerInMainJTree(project, clickedMethod)) {
            return;
        }
        if (NavigationUtils.navigationScheduledInMainJTree(project, clickedMethod, qualifiedName)) {
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
     * 静态方式，刷新视图
     *
     * @param project
     */
    public static void staticRefreshView(@NotNull Project project) {
        ApplicationManager.getApplication().runReadAction(new Runnable() {
            @Override
            public void run() {
                SpringMvcControllerScan springMvcControllerScan = new SpringMvcControllerScan();
                SpringScheduledScan springScheduledScan = new SpringScheduledScan();
                List<Controller> staticControllers = springMvcControllerScan.scan(project);
                List<SpringScheduled> staticSchedules = springScheduledScan.scan(project);
                assert project != null;
                Objects.requireNonNull(project.getUserData(CoolRequestConfigConstant.UserProjectManagerKey)).addComponent(staticControllers);
                Objects.requireNonNull(project.getUserData(CoolRequestConfigConstant.UserProjectManagerKey)).addComponent(staticSchedules);

            }
        });
    }

    /**
     * Controller->Code跳转
     *
     * @param project
     * @param controller
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
        if (psiClass != null) {
            PsiMethod httpMethodMethodInClass = findHttpMethodInClass(psiClass,
                    controller.getMethodName(),
                    controller.getHttpMethod(),
                    controller.getParamClassList(), controller.getUrl());
            if (httpMethodMethodInClass != null) PsiUtils.methodNavigate(httpMethodMethodInClass);
        }
    }

    public static void jumpToSpringScheduledMethod(Project project, SpringScheduled springScheduled) {
        PsiClass psiClass = findClassByName(project, springScheduled.getModuleName(), springScheduled.getClassName());
        if (psiClass != null) {
            PsiMethod methodInClass = findMethodInClassOne(psiClass, springScheduled.getMethodName());
            if (methodInClass != null) PsiUtils.methodNavigate(methodInClass);
        }
    }
}
