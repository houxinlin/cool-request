package com.cool.request.action.export;

import com.cool.request.common.bean.components.controller.Controller;
import com.cool.request.common.icons.CoolRequestIcons;
import com.cool.request.plugin.apipost.ApipostExport;
import com.cool.request.utils.ProgressWindowWrapper;
import com.cool.request.view.main.MainTopTreeView;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ApipostExportAnAction extends AnAction {
    private final MainTopTreeView mainTopTreeView;

    public ApipostExportAnAction(MainTopTreeView mainTopTreeView) {
        super(() -> "Apipost", CoolRequestIcons.APIPOST);
        this.mainTopTreeView = mainTopTreeView;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        ApipostExport apipostExport = new ApipostExport(e.getProject());
        ProgressWindowWrapper.newProgressWindowWrapper(e.getProject()).run(new Task.Backgroundable(e.getProject(), "Connecting...") {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                if (apipostExport.canExport()) {
                    List<Controller> requestMappingModels = mainTopTreeView.getSelectController();
                    apipostExport.export(requestMappingModels);
                    return;
                }
                apipostExport.showCondition();

            }
        });
    }
}
