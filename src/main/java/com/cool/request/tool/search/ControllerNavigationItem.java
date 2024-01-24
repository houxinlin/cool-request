package com.cool.request.tool.search;

import com.cool.request.bean.components.controller.Controller;
import com.cool.request.utils.HttpMethodIconUtils;
import com.cool.request.utils.NavigationUtils;
import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

import static com.cool.request.utils.PsiUtils.findClassByName;
import static com.cool.request.utils.PsiUtils.findHttpMethodInClass;

public class ControllerNavigationItem extends Controller implements NavigationItem {
    private Project project;

    public ControllerNavigationItem(Controller controller, Project project) {
        setMethodName(controller.getMethodName());
        setUrl(controller.getUrl());
        setContextPath(controller.getContextPath());
        setServerPort(controller.getServerPort());
        setHttpMethod(controller.getHttpMethod());
        setId(controller.getId());
        setSimpleClassName(controller.getSimpleClassName());
        setModuleName(controller.getModuleName());
        this.project = project;
    }

    @Override
    public @Nullable String getName() {
        return getUrl();
    }

    @Override
    public @Nullable ItemPresentation getPresentation() {
        return new ItemPresentation() {
            @Override
            public @Nullable String getPresentableText() {
                return getUrl();
            }

            @Override
            public @Nullable String getLocationString() {
                return getServerPort() + ":" + getSimpleClassName() + "." + getMethodName();
            }

            @Override
            public @Nullable Icon getIcon(boolean unused) {
                return HttpMethodIconUtils.getIconByHttpMethod(getHttpMethod());
            }
        };
    }

    @Override
    public void navigate(boolean requestFocus) {
        PsiClass psiClass = findClassByName(project, getModuleName(), getSimpleClassName());
        if (psiClass != null) {
            PsiMethod httpMethodMethodInClass = findHttpMethodInClass(psiClass,
                    getMethodName(),
                    getHttpMethod(),
                    getParamClassList(), getUrl());
            if (httpMethodMethodInClass != null) {
                httpMethodMethodInClass.navigate(true);
                NavigationUtils.navigationControllerInMainJTree(project, httpMethodMethodInClass);
            }
        }
    }

    @Override
    public boolean canNavigate() {
        return true;
    }

    @Override
    public boolean canNavigateToSource() {
        return true;
    }
}
