/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * OpenApiExportAnAction.java is part of Cool Request
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
import com.cool.request.utils.file.FileChooseUtils;
import com.cool.request.view.main.MainTopTreeView;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class OpenApiExportAnAction extends AnAction {
    private final MainTopTreeView mainTopTreeView;

    public OpenApiExportAnAction(MainTopTreeView mainTopTreeView) {
        super("Openapi", "Openapi", CoolRequestIcons.OPENAPI);
        this.mainTopTreeView = mainTopTreeView;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        String storagePath = FileChooseUtils.chooseFileSavePath(null, mainTopTreeView.getProject().getName() + ".json", e.getProject());
        if (storagePath != null) {
            List<Controller> selectRequestMappings = mainTopTreeView.getSelectController();
            try {
                Files.write(Paths.get(storagePath), OpenApiUtils.toOpenApiJson(mainTopTreeView.getProject(),selectRequestMappings).getBytes(StandardCharsets.UTF_8));
            } catch (IOException ignored) {
            }
        }
    }
}
