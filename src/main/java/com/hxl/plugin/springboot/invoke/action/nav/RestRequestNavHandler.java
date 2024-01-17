package com.hxl.plugin.springboot.invoke.action.nav;

import com.hxl.plugin.springboot.invoke.utils.NavigationUtils;
import com.intellij.codeInsight.daemon.GutterIconNavigationHandler;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;

import javax.swing.*;
import java.awt.event.MouseEvent;

/**
 * @author zhangpj
 * @date 2024/01/17
 */
public class RestRequestNavHandler implements GutterIconNavigationHandler<PsiElement> {

    @Override
    public void navigate(MouseEvent e, PsiElement elt) {
        Project project = elt.getProject();
        PsiMethod method = (PsiMethod) elt.getParent();
        if (SwingUtilities.isLeftMouseButton(e)) {
            NavigationUtils.jumpToNavigation(project, method);
        }
        if (SwingUtilities.isRightMouseButton(e)) {
            method.navigate(true);
        }
    }

}
