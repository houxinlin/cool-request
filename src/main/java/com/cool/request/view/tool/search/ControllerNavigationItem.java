package com.cool.request.view.tool.search;

import com.cool.request.common.bean.components.controller.Controller;
import com.cool.request.component.CanMark;
import com.cool.request.utils.HttpMethodIconUtils;
import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

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
        setOwnerPsiMethod(controller.getOwnerPsiMethod());
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
        goToCode(project);
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
