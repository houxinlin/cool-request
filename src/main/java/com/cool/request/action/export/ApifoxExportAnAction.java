/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * ApifoxExportAnAction.java is part of Cool Request
 *
 * License: GPL-3.0+
 *
 * Cool Request is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Cool Request is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Cool Request.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.cool.request.action.export;

import com.cool.request.components.http.Controller;
import com.cool.request.common.icons.CoolRequestIcons;
import com.cool.request.lib.openapi.OpenApiUtils;
import com.cool.request.plugin.apifox.ApiFoxExport;
import com.cool.request.utils.CursorUtils;
import com.cool.request.utils.ProgressWindowWrapper;
import com.cool.request.utils.ResourceBundleUtils;
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
                    Messages.showErrorDialog("No Api to export", ResourceBundleUtils.getString("tip"));
                    return;
                }

                SwingUtilities.invokeLater(() -> apifoxExp.export(OpenApiUtils.toOpenApiJson(mainTopTreeView.getProject(), requestMappingModels.stream()
                        .distinct()
                        .collect(Collectors.toList()), false)));
            }
        });
    }
}
