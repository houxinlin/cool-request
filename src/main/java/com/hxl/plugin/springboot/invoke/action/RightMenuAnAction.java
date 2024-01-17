package com.hxl.plugin.springboot.invoke.action;

import com.hxl.plugin.springboot.invoke.utils.NavigationUtils;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;

public class RightMenuAnAction extends AnAction {

    /**
     * This method is called when the action is performed.
     *
     * @param e The action event that occurred.
     */
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        assert project != null;
        PsiMethod clickedMethod = NavigationUtils.findClickedMethod(e);
        if (clickedMethod == null) {
            return;
        }
        NavigationUtils.jumpToNavigation(project, clickedMethod);
    }

}
