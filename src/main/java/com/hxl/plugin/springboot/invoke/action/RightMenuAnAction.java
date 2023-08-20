package com.hxl.plugin.springboot.invoke.action;

import com.hxl.plugin.springboot.invoke.view.PluginWindowView;
import com.hxl.plugin.springboot.invoke.view.main.MainTopTreeView;
import com.intellij.openapi.actionSystem.*;
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

public class RightMenuAnAction  extends AnAction {
    private PsiMethod findClickedMethod(PsiFile psiFile, AnActionEvent e) {
        int offset = e.getData(PlatformDataKeys.CARET).getOffset();
        PsiElement element = psiFile.findElementAt(offset);
        return PsiTreeUtil.getParentOfType(element, PsiMethod.class);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project != null) {
            PsiFile psiFile = e.getData(LangDataKeys.PSI_FILE);
            if (psiFile != null) {
                PsiMethod clickedMethod = findClickedMethod(psiFile, e);
                if (clickedMethod != null) {
                    ToolWindow springBootInvoke = ToolWindowManager.getInstance(project).getToolWindow("SpringBootInvoke");
                    String qualifiedName = clickedMethod.getContainingClass().getQualifiedName();
                    JComponent mainComponent = springBootInvoke.getContentManager().getSelectedContent().getComponent();
                    if (mainComponent instanceof PluginWindowView){
                        PluginWindowView pluginWindowView = (PluginWindowView) mainComponent;
                        for (List<MainTopTreeView.RequestMappingNode> value : pluginWindowView.getMainTopTreeView().getRequestMappingNodeMap().values()) {
                            for (MainTopTreeView.RequestMappingNode requestMappingNode :value) {
                                if (requestMappingNode.getData().getController().getSimpleClassName().equals(qualifiedName) &&
                                        clickedMethod.getName().equals(requestMappingNode.getData().getController().getMethodName())){
                                    pluginWindowView.getMainBottomHTTPContainer().controllerChooseEvent(requestMappingNode.getData());
                                    pluginWindowView.getMainTopTreeView().selectNode(requestMappingNode);
                                    return;
                                }
                            }
                        }

                        for (List<MainTopTreeView.ScheduledMethodNode> value : pluginWindowView.getMainTopTreeView().getScheduleMapNodeMap().values()) {
                            for (MainTopTreeView.ScheduledMethodNode scheduledMethodNode :value) {
                                if (scheduledMethodNode.getData().getClassName().equals(qualifiedName) &&
                                        clickedMethod.getName().equals(scheduledMethodNode.getData().getMethodName())){
                                    pluginWindowView.getMainBottomHTTPContainer().scheduledChooseEvent(scheduledMethodNode.getData());
                                    pluginWindowView.getMainTopTreeView().selectNode(scheduledMethodNode);
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }

//        Project project = e.getProject();
//        Editor editor = e.getData(CommonDataKeys.EDITOR);
//        PsiElement psiElement = e.getData(CommonDataKeys.PSI_ELEMENT);
//        if (project == null || project.isDefault() || editor == null || !(psiElement instanceof PsiMethod)) {
//            return;
//        }
//        PsiMethod positionMethod = (PsiMethod) psiElement;
//
//        String methodName = positionMethod.getName();
//        System.out.println("Clicked Method Name: " + methodName);
//        e.getDataContext()
//        Editor editor = (Editor) e.getDataContext().getData("editor");
//        System.out.println(editor);
//        if (editor != null) {
//            Project project = e.getProject();
//            PsiElement elementAtCaret = PsiTreeUtil.findElementOfClassAtOffset(editor.getDocument()., editor.getCaretModel().getOffset(), PsiElement.class, false);
//
//            if (elementAtCaret != null) {
//                String className = elementAtCaret.getContainingFile().getContainingDirectory().getName();
//                String methodName = elementAtCaret.getText();
//                System.out.println("Class name: " + className);
//                System.out.println("Method name: " + methodName);
//            }
//        }
    }
}
