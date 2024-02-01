package com.cool.request.common.service;

import com.cool.request.view.component.ApiToolPage;
import com.cool.request.view.component.MainBottomHTTPContainer;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;

@Service
public final class ProjectViewSingleton {
    private MainBottomHTTPContainer mainBottomHTTPContainer;

    private ApiToolPage apiToolPage;
    private Project project;

    public void setProject(Project project) {
        this.project = project;
    }

    public static ProjectViewSingleton getInstance(Project project) {
        ProjectViewSingleton service = project.getService(ProjectViewSingleton.class);
        service.setProject(project);
        return service;
    }
    public ApiToolPage createAndApiToolPage() {
        if (apiToolPage == null) apiToolPage = new ApiToolPage(project);
        return apiToolPage;
    }
    public MainBottomHTTPContainer createAndGetMainBottomHTTPContainer() {
        if (mainBottomHTTPContainer == null) mainBottomHTTPContainer = new MainBottomHTTPContainer(project);
        return mainBottomHTTPContainer;
    }
}
