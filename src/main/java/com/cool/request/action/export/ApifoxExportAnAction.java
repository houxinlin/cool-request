package com.cool.request.action.export;

import com.cool.request.common.bean.components.controller.Controller;
import com.cool.request.common.icons.CoolRequestIcons;
import com.cool.request.lib.openapi.OpenApiUtils;
import com.cool.request.plugin.apifox.ApiFoxExport;
import com.cool.request.utils.CursorUtils;
import com.cool.request.utils.ProgressWindowWrapper;
import com.cool.request.view.main.MainTopTreeView;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.treeStructure.SimpleTree;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.List;
import java.util.stream.Collectors;

public class ApifoxExportAnAction extends AnAction {
    private final SimpleTree simpleTree;
    private final ApiFoxExport apifoxExp;
    private final MainTopTreeView mainTopTreeView;

    public ApifoxExportAnAction(MainTopTreeView mainTopTreeView) {
        super("Apifox", "Apifox", CoolRequestIcons.APIFOX);
        this.simpleTree = ((SimpleTree) mainTopTreeView.getTree());
        this.mainTopTreeView = mainTopTreeView;
        this.apifoxExp = new ApiFoxExport(mainTopTreeView.getProject());
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        ProgressWindowWrapper.newProgressWindowWrapper(e.getProject()).run(new Task.Backgroundable(e.getProject(), "Connecting...") {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                CursorUtils.setWait(simpleTree);
                boolean result = apifoxExp.canExport();
                CursorUtils.setDefault(simpleTree);
                if (!result) {
                    apifoxExp.showCondition();
                    return;
                }
                List<Controller> requestMappingModels = mainTopTreeView.getSelectController();
                if (requestMappingModels.isEmpty()) {
                    Messages.showErrorDialog("No Api to export", "Tip");
                    return;
                }

                SwingUtilities.invokeLater(() -> apifoxExp.export(OpenApiUtils.toOpenApiJson(mainTopTreeView.getProject(), requestMappingModels.stream()
                        .distinct()
                        .collect(Collectors.toList()), false)));
            }
        });
    }
}
