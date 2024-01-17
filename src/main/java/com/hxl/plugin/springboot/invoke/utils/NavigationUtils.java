package com.hxl.plugin.springboot.invoke.utils;

import com.hxl.plugin.springboot.invoke.Constant;
import com.hxl.plugin.springboot.invoke.IdeaTopic;
import com.hxl.plugin.springboot.invoke.bean.components.controller.Controller;
import com.hxl.plugin.springboot.invoke.net.HttpMethod;
import com.hxl.plugin.springboot.invoke.scans.controller.SpringMvcControllerScan;
import com.hxl.plugin.springboot.invoke.scans.scheduled.SpringScheduledScan;
import com.hxl.plugin.springboot.invoke.springmvc.utils.ParamUtils;
import com.hxl.plugin.springboot.invoke.view.CoolIdeaPluginWindowView;
import com.hxl.plugin.springboot.invoke.view.main.MainTopTreeView;
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
import com.intellij.ui.content.Content;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.List;
import java.util.Objects;

import static com.hxl.plugin.springboot.invoke.Constant.PLUGIN_ID;

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
     * @param project                  The current project.
     * @param clickedMethod            The clicked method.
     * @param qualifiedName            The qualified name of the clicked method.
     * @return True if the scheduled task was found, false otherwise.
     */
    public static boolean navigationScheduledInMainJTree(Project project,
                                                         PsiMethod clickedMethod,
                                                         String qualifiedName) {
        CoolIdeaPluginWindowView coolIdeaPluginWindowView = project.getUserData(Constant.CoolIdeaPluginWindowViewKey);
        if (coolIdeaPluginWindowView == null) return false;

        for (List<MainTopTreeView.ScheduledMethodNode> value : coolIdeaPluginWindowView.getMainTopTreeView().getScheduleMapNodeMap().values()) {
            for (MainTopTreeView.ScheduledMethodNode scheduledMethodNode : value) {
                if (scheduledMethodNode.getData().getClassName().equals(qualifiedName) &&
                        clickedMethod.getName().equals(scheduledMethodNode.getData().getMethodName())) {
                    project.getMessageBus()
                            .syncPublisher(IdeaTopic.SCHEDULED_CHOOSE_EVENT)
                            .onChooseEvent(scheduledMethodNode.getData());
                    coolIdeaPluginWindowView.getMainTopTreeView().selectNode(scheduledMethodNode);
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
        CoolIdeaPluginWindowView coolIdeaPluginWindowView = project.getUserData(Constant.CoolIdeaPluginWindowViewKey);
        if (coolIdeaPluginWindowView == null) return false;

        List<HttpMethod> supportMethod = PsiUtils.getHttpMethod(psiMethod);
        if (supportMethod.isEmpty()) {
            return false;
        }
        List<String> httpUrl = ParamUtils.getHttpUrl(psiMethod);
        if (httpUrl == null) {
            return false;
        }
        String methodClassName = "";
        PsiClass containingClass = psiMethod.getContainingClass();
        if (containingClass != null) {
            methodClassName = psiMethod.getContainingClass().getQualifiedName();
        }

        String methodName = psiMethod.getName();
        MainTopTreeView.RequestMappingNode result = null;
        int max = -1;

        for (List<MainTopTreeView.RequestMappingNode> value : coolIdeaPluginWindowView.getMainTopTreeView().getRequestMappingNodeMap().values()) {
            for (MainTopTreeView.RequestMappingNode requestMappingNode : value) {
                Controller controller = requestMappingNode.getData();

                if (controller.getSimpleClassName().equals(methodClassName) &&
                        ParamUtils.httpMethodIn(supportMethod, HttpMethod.parse(controller.getHttpMethod()))) {

                    if (methodName.equals(controller.getMethodName()) &&
                            ParamUtils.isEquals(controller.getParamClassList(), PsiUtils.getParamClassList(psiMethod))) {
                        project.getMessageBus()
                                .syncPublisher(IdeaTopic.CONTROLLER_CHOOSE_EVENT)
                                .onChooseEvent(requestMappingNode.getData());
                        coolIdeaPluginWindowView.getMainTopTreeView().selectNode(requestMappingNode);
                        return true;
                    } else {
                        for (String urlItem : httpUrl) {
                            if (controller.getUrl().endsWith(urlItem) &&
                                    urlItem.length() > max && ParamUtils.httpMethodIn(supportMethod, HttpMethod.parse(controller.getHttpMethod()))) {
                                max = urlItem.length();
                                result = requestMappingNode;
                            }
                        }
                    }
                }
            }
        }
        if (result != null) {
            project.getMessageBus()
                    .syncPublisher(IdeaTopic.CONTROLLER_CHOOSE_EVENT)
                    .onChooseEvent(result.getData());
            coolIdeaPluginWindowView.getMainTopTreeView().selectNode(result);
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
        String qualifiedName = "";
        if (clickedMethod.getContainingClass() != null) {
            qualifiedName = clickedMethod.getContainingClass().getQualifiedName();
        }
        Content content = toolWindow.getContentManager().getSelectedContent();
        if (content == null) {
            return;
        }
        JComponent mainComponent = content.getComponent();
        if (mainComponent instanceof CoolIdeaPluginWindowView) {
            CoolIdeaPluginWindowView coolIdeaPluginWindowView = (CoolIdeaPluginWindowView) mainComponent;
            toolWindow.show();
            if (NavigationUtils.navigationControllerInMainJTree(project, clickedMethod)) {
                return;
            }
            if (NavigationUtils.navigationScheduledInMainJTree(project, clickedMethod, qualifiedName)) {
                return;
            }
            NotifyUtils.notification(project, ResourceBundleUtils.getString("method.not.fount"));
        }
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
     * @param project
     */
    public static void staticRefreshView(@NotNull Project project) {
        SpringMvcControllerScan springMvcControllerScan = new SpringMvcControllerScan();
        SpringScheduledScan springScheduledScan =new SpringScheduledScan();
        List<Controller> staticControllerScanResult = springMvcControllerScan.scan(project);
        assert project != null;
        Objects.requireNonNull(project.getUserData(Constant.UserProjectManagerKey)).addComponent(staticControllerScanResult);
        Objects.requireNonNull(project.getUserData(Constant.UserProjectManagerKey)).addComponent(springScheduledScan.scan(project));
    }

}
