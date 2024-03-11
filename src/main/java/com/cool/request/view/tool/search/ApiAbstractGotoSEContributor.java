package com.cool.request.view.tool.search;

import com.cool.request.common.bean.components.controller.Controller;
import com.cool.request.common.constant.CoolRequestConfigConstant;
import com.cool.request.component.api.scans.SpringMvcControllerScan;
import com.cool.request.view.tool.UserProjectManager;
import com.intellij.ide.actions.searcheverywhere.AbstractGotoSEContributor;
import com.intellij.ide.actions.searcheverywhere.FoundItemDescriptor;
import com.intellij.ide.util.gotoByName.FilteringGotoByModel;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.ThrowableComputable;
import com.intellij.psi.codeStyle.MinusculeMatcher;
import com.intellij.psi.codeStyle.NameUtil;
import com.intellij.util.Processor;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

public class ApiAbstractGotoSEContributor extends AbstractGotoSEContributor {
    private SpringMvcControllerScan scan;
    private List<ControllerNavigationItem> allController;
    private AnActionEvent event;
    private Project myProject;

    public ApiAbstractGotoSEContributor(@NotNull AnActionEvent event) {
        super(event);
        scan = new SpringMvcControllerScan();
        this.event = event;
        this.myProject = event.getProject();
    }

    @Override
    protected @NotNull FilteringGotoByModel<?> createModel(@NotNull Project project) {
        return null;
    }


    @Override
    public boolean processSelectedItem(@NotNull Object selected, int modifiers, @NotNull String searchText) {
        return super.processSelectedItem(selected, modifiers, searchText);
    }

    @Override
    public Object getDataForItem(@NotNull Object element, @NotNull String dataId) {
        return null;
    }


    @Override
    public @NotNull String getSearchProviderId() {
        return ApiAbstractGotoSEContributor.class.getSimpleName();
    }

    @Override
    public boolean isEmptyPatternSupported() {
        return true;
    }


    @Override
    public boolean showInFindResults() {
        return false;
    }

    @Override
    public boolean isDumbAware() {
        return DumbService.isDumb(myProject);
    }


    @Override
    public void fetchWeightedElements(@NotNull String pattern, @NotNull ProgressIndicator progressIndicator,
                                      @NotNull Processor<? super FoundItemDescriptor<Object>> consumer) {
        if (isDumbAware()) {
            return;
        }
        progressIndicator.start();
        if (allController == null || allController.size() == 0) {
            try {
                UserProjectManager userData = myProject.getUserData(CoolRequestConfigConstant.UserProjectManagerKey);
                if (userData != null) {
                    allController = userData.getController()
                            .stream()
                            .map(controller -> new ControllerNavigationItem(controller, myProject))
                            .collect(Collectors.toList());

                }
                if (allController == null || allController.isEmpty()) {
                    allController = ApplicationManager.getApplication().runReadAction(
                                    (ThrowableComputable<List<Controller>, Throwable>) () ->
                                            scan.scan(event.getProject())
                            ).stream()
                            .map(controller -> new ControllerNavigationItem(controller, myProject))
                            .collect(Collectors.toList());
                }
            } catch (Throwable e) {
            }
        }

        MinusculeMatcher matcher = NameUtil.buildMatcher("*" + removeParam(pattern) + "*", NameUtil.MatchingCaseSensitivity.NONE);
        for (Controller controller : allController) {
            if (matcher.matches(controller.getUrl())) {
                if (!consumer.process(new FoundItemDescriptor<>(controller, 0))) {
                    return;
                }
            }
        }

    }

    private static String removeParam(String url) {
        try {
            URL newUrl = new URL(url);
            return newUrl.getPath();
        } catch (MalformedURLException e) {
        }
        if (url.contains("?")) url = url.substring(0, url.indexOf('?'));
        return url;
    }

    @Override
    public boolean isShownInSeparateTab() {
        return true;
    }


    @Override
    public @NotNull @Nls String getGroupName() {
        return "Cool Request";
    }


    @Override
    public int getSortWeight() {
        return 888;
    }
}
