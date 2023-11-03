package com.hxl.plugin.springboot.invoke.action;

import com.hxl.plugin.springboot.invoke.view.CoolIdeaPluginWindowView;
import com.hxl.plugin.springboot.invoke.view.main.MainTopTreeView;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.List;

public class RightMenuAnAction  extends AnAction {
    private PsiMethod findClickedMethod(PsiFile psiFile, AnActionEvent e) {
        PsiElement psiElement = e.getData(PlatformDataKeys.PSI_ELEMENT);
        if (psiElement != null) {
            PsiManager manager = psiElement.getManager();
            IElementType elementType = psiElement.getNode().getElementType();
        }
        int offset = e.getData(PlatformDataKeys.CARET).getOffset();
        PsiElement element = psiFile.findElementAt(offset);
        return PsiTreeUtil.getParentOfType(element, PsiMethod.class);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow("SpringBootInvoke");
        if(toolWindow != null && !toolWindow.isActive()) {
            toolWindow.activate(null);
        }
        PsiFile psiFile = e.getData(LangDataKeys.PSI_FILE);
        if (psiFile != null) {
            PsiMethod clickedMethod = findClickedMethod(psiFile, e);
            if (clickedMethod != null) {
                ToolWindow springBootInvoke = ToolWindowManager.getInstance(project).getToolWindow("SpringBootInvoke");
                String qualifiedName = clickedMethod.getContainingClass().getQualifiedName();
                JComponent mainComponent = springBootInvoke.getContentManager().getSelectedContent().getComponent();
                if (mainComponent instanceof CoolIdeaPluginWindowView){
                    CoolIdeaPluginWindowView coolIdeaPluginWindowView = (CoolIdeaPluginWindowView) mainComponent;
                    for (List<MainTopTreeView.RequestMappingNode> value : coolIdeaPluginWindowView.getMainTopTreeView().getRequestMappingNodeMap().values()) {
                        for (MainTopTreeView.RequestMappingNode requestMappingNode :value) {
                            if (requestMappingNode.getData().getController().getSimpleClassName().equals(qualifiedName) &&
                                    clickedMethod.getName().equals(requestMappingNode.getData().getController().getMethodName())){
                                coolIdeaPluginWindowView.getMainBottomHTTPContainer().controllerChooseEvent(requestMappingNode.getData());
                                coolIdeaPluginWindowView.getMainTopTreeView().selectNode(requestMappingNode);
                                return;
                            }
                        }
                    }

                    for (List<MainTopTreeView.ScheduledMethodNode> value : coolIdeaPluginWindowView.getMainTopTreeView().getScheduleMapNodeMap().values()) {
                        for (MainTopTreeView.ScheduledMethodNode scheduledMethodNode :value) {
                            if (scheduledMethodNode.getData().getClassName().equals(qualifiedName) &&
                                    clickedMethod.getName().equals(scheduledMethodNode.getData().getMethodName())){
                                coolIdeaPluginWindowView.getMainBottomHTTPContainer().scheduledChooseEvent(scheduledMethodNode.getData());
                                coolIdeaPluginWindowView.getMainTopTreeView().selectNode(scheduledMethodNode);
                                return;
                            }
                        }
                    }
                }
            }
        }

    }
}
