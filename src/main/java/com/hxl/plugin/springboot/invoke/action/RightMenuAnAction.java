package com.hxl.plugin.springboot.invoke.action;

import com.hxl.plugin.springboot.invoke.IdeaTopic;
import com.hxl.plugin.springboot.invoke.model.SpringMvcRequestMappingSpringInvokeEndpoint;
import com.hxl.plugin.springboot.invoke.net.HttpMethod;
import com.hxl.plugin.springboot.invoke.springmvc.utils.ParamUtils;
import com.hxl.plugin.springboot.invoke.utils.NotifyUtils;
import com.hxl.plugin.springboot.invoke.utils.PsiUtils;
import com.hxl.plugin.springboot.invoke.utils.ResourceBundleUtils;
import com.hxl.plugin.springboot.invoke.view.CoolIdeaPluginWindowView;
import com.hxl.plugin.springboot.invoke.view.main.MainTopTreeView;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.ui.content.Content;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.List;
import java.util.Optional;

import static com.hxl.plugin.springboot.invoke.Constant.PLUGIN_ID;

public class RightMenuAnAction extends AnAction {
    private PsiMethod findClickedMethod(AnActionEvent e) {
        PsiFile psiFile = e.getData(LangDataKeys.PSI_FILE);
        if (psiFile == null) return null;
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


    private boolean queryControllerNode(Project project, CoolIdeaPluginWindowView coolIdeaPluginWindowView, PsiMethod psiMethod) {
        List<HttpMethod> supportMethod = PsiUtils.getHttpMethod(psiMethod);
        if (supportMethod.isEmpty()) return false;
        String[] httpUrl = ParamUtils.getHttpUrl(psiMethod);
        if (httpUrl == null) return false;
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
                SpringMvcRequestMappingSpringInvokeEndpoint controller = requestMappingNode.getData().getController();

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
                        for (String urlItem : Optional.ofNullable(httpUrl).orElse(new String[]{})) {
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

    private boolean queryScheduled(Project project, CoolIdeaPluginWindowView coolIdeaPluginWindowView, PsiMethod clickedMethod, String qualifiedName) {
        for (List<MainTopTreeView.ScheduledMethodNode> value : coolIdeaPluginWindowView.getMainTopTreeView().getScheduleMapNodeMap().values()) {
            for (MainTopTreeView.ScheduledMethodNode scheduledMethodNode : value) {
                if (scheduledMethodNode.getData().getSpringScheduledSpringInvokeEndpoint().getClassName().equals(qualifiedName) &&
                        clickedMethod.getName().equals(scheduledMethodNode.getData().getSpringScheduledSpringInvokeEndpoint().getMethodName())) {
                    project.getMessageBus()
                            .syncPublisher(IdeaTopic.SCHEDULED_CHOOSE_EVENT)
                            .onChooseEvent(scheduledMethodNode.getData().getSpringScheduledSpringInvokeEndpoint(), scheduledMethodNode.getData().getPort());
                    coolIdeaPluginWindowView.getMainTopTreeView().selectNode(scheduledMethodNode);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        assert project != null;
        ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow(PLUGIN_ID);
        if (toolWindow == null) return;
        if (!toolWindow.isActive()) {
            toolWindow.activate(null);
        }
        PsiMethod clickedMethod = findClickedMethod(e);
        if (clickedMethod == null) return;
        String qualifiedName = "";
        if (clickedMethod.getContainingClass() != null) {
            qualifiedName = clickedMethod.getContainingClass().getQualifiedName();
        }
        Content content = toolWindow.getContentManager().getSelectedContent();
        if (content == null) return;
        JComponent mainComponent = content.getComponent();
        if (mainComponent instanceof CoolIdeaPluginWindowView) {
            CoolIdeaPluginWindowView coolIdeaPluginWindowView = (CoolIdeaPluginWindowView) mainComponent;
            toolWindow.show();
            if (queryControllerNode(project, coolIdeaPluginWindowView, clickedMethod)) return;

            if (queryScheduled(project, coolIdeaPluginWindowView, clickedMethod, qualifiedName)) return;

            NotifyUtils.notification(project, ResourceBundleUtils.getString("method.not.fount"));

        }

    }


}
