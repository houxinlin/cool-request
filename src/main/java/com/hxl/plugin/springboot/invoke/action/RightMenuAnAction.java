package com.hxl.plugin.springboot.invoke.action;

import com.hxl.plugin.springboot.invoke.utils.NavigationUtils;
import com.hxl.plugin.springboot.invoke.utils.NotifyUtils;
import com.hxl.plugin.springboot.invoke.utils.ResourceBundleUtils;
import com.hxl.plugin.springboot.invoke.view.CoolIdeaPluginWindowView;
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
import com.intellij.ui.content.Content;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

import static com.hxl.plugin.springboot.invoke.Constant.PLUGIN_ID;

public class RightMenuAnAction extends AnAction {

    /**
     * This method finds the method clicked on in the editor.
     *
     * @param e The action event that occurred.
     * @return The method that was clicked on or null if no method was clicked on.
     */
    private PsiMethod findClickedMethod(AnActionEvent e) {
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
     * This method is called when the action is performed.
     *
     * @param e The action event that occurred.
     */
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        assert project != null;
        ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow(PLUGIN_ID);
        if (toolWindow == null) {
            return;
        }
        if (!toolWindow.isActive()) {
            toolWindow.activate(null);
        }
        PsiMethod clickedMethod = findClickedMethod(e);
        if (clickedMethod == null) {
            return;
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


}
