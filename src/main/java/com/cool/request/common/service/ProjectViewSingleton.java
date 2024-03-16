package com.cool.request.common.service;

import com.cool.request.common.constant.CoolRequestConfigConstant;
import com.cool.request.component.CoolRequestContext;
import com.cool.request.component.CoolRequestPluginDisposable;
import com.cool.request.view.component.CoolRequestView;
import com.cool.request.view.component.MainBottomHTTPContainer;
import com.cool.request.view.tool.ProviderManager;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;

@Service
public final class ProjectViewSingleton {
    private MainBottomHTTPContainer mainBottomHTTPContainer;

    private CoolRequestView coolRequestView;
    private Project project;

    public void setProject(Project project) {
        this.project = project;
    }

    public static ProjectViewSingleton getInstance(Project project) {
        ProjectViewSingleton service = project.getService(ProjectViewSingleton.class);
        service.setProject(project);
        return service;
    }

    public CoolRequestView createAndApiToolPage() {
        if (coolRequestView == null) coolRequestView = new CoolRequestView(project);
        return coolRequestView;
    }

    public MainBottomHTTPContainer createAndGetMainBottomHTTPContainer() {
        if (mainBottomHTTPContainer == null) {
            mainBottomHTTPContainer = new MainBottomHTTPContainer(project);
            Disposer.register(CoolRequestPluginDisposable.getInstance(project), mainBottomHTTPContainer);
        }

        ProviderManager.registerProvider(MainBottomHTTPContainer.class, CoolRequestConfigConstant.MainBottomHTTPContainerKey, mainBottomHTTPContainer, project);
        CoolRequestContext coolRequestContext = CoolRequestContext.getInstance(project);
        coolRequestContext.setMainBottomHTTPContainer(mainBottomHTTPContainer);
        coolRequestContext.setMainRequestParamManager(mainBottomHTTPContainer.getMainBottomHttpInvokeViewPanel().getHttpRequestParamPanel());
        return mainBottomHTTPContainer;
    }
}
