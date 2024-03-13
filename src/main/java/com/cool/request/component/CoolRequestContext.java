package com.cool.request.component;

import com.cool.request.view.component.MainBottomHTTPContainer;
import com.cool.request.view.main.IRequestParamManager;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

@Service
public final class CoolRequestContext {
    private MainBottomHTTPContainer mainBottomHTTPContainer;
    private IRequestParamManager mainRequestParamManager;

    public void setMainRequestParamManager(IRequestParamManager mainRequestParamManager) {
        this.mainRequestParamManager = mainRequestParamManager;
    }

    public static CoolRequestContext getInstance(Project project) {
        return project.getService(CoolRequestContext.class);
    }

    @NotNull
    public IRequestParamManager getMainRequestParamManager() {
        return mainBottomHTTPContainer.getMainBottomHttpInvokeViewPanel().getHttpRequestParamPanel();
    }

    public void setMainBottomHTTPContainer(@NotNull MainBottomHTTPContainer mainBottomHTTPContainer) {
        this.mainBottomHTTPContainer = mainBottomHTTPContainer;
    }

    @NotNull
    public MainBottomHTTPContainer getMainBottomHTTPContainer() {
        return mainBottomHTTPContainer;
    }
}
