package com.hxl.plugin.springboot.invoke.action;

import com.hxl.plugin.springboot.invoke.IdeaTopic;
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
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.List;

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

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        assert project != null;
        ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow(PLUGIN_ID);

        if (toolWindow != null && !toolWindow.isActive()) {
            toolWindow.activate(null);
        }
        PsiMethod clickedMethod = findClickedMethod(e);
        if (clickedMethod == null) return;
        String qualifiedName = clickedMethod.getContainingClass().getQualifiedName();
        JComponent mainComponent = toolWindow.getContentManager().getSelectedContent().getComponent();
        if (mainComponent instanceof CoolIdeaPluginWindowView) {
            CoolIdeaPluginWindowView coolIdeaPluginWindowView = (CoolIdeaPluginWindowView) mainComponent;
            for (List<MainTopTreeView.RequestMappingNode> value : coolIdeaPluginWindowView.getMainTopTreeView().getRequestMappingNodeMap().values()) {
                for (MainTopTreeView.RequestMappingNode requestMappingNode : value) {
                    if (requestMappingNode.getData().getController().getSimpleClassName().equals(qualifiedName) &&
                            clickedMethod.getName().equals(requestMappingNode.getData().getController().getMethodName())) {
                        project.getMessageBus()
                                .syncPublisher(IdeaTopic.CONTROLLER_CHOOSE_EVENT)
                                .onChooseEvent(requestMappingNode.getData());
                        coolIdeaPluginWindowView.getMainTopTreeView().selectNode(requestMappingNode);
                        toolWindow.show();
                        return;
                    }
                }
            }
            for (List<MainTopTreeView.ScheduledMethodNode> value : coolIdeaPluginWindowView.getMainTopTreeView().getScheduleMapNodeMap().values()) {
                for (MainTopTreeView.ScheduledMethodNode scheduledMethodNode : value) {
                    if (scheduledMethodNode.getData().getSpringScheduledSpringInvokeEndpoint().getClassName().equals(qualifiedName) &&
                            clickedMethod.getName().equals(scheduledMethodNode.getData().getSpringScheduledSpringInvokeEndpoint().getMethodName())) {
                        project.getMessageBus()
                                .syncPublisher(IdeaTopic.SCHEDULED_CHOOSE_EVENT)
                                .onChooseEvent(scheduledMethodNode.getData().getSpringScheduledSpringInvokeEndpoint(), scheduledMethodNode.getData().getPort());
                        coolIdeaPluginWindowView.getMainTopTreeView().selectNode(scheduledMethodNode);
                        toolWindow.show();
                        return;
                    }
                }
            }
        }

    }
}
