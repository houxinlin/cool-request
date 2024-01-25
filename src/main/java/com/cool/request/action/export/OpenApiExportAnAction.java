package com.cool.request.action.export;

import com.cool.request.bean.components.controller.Controller;
import com.cool.request.icons.MyIcons;
import com.cool.request.openapi.OpenApiUtils;
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
        super("Openapi", "Openapi", MyIcons.OPENAPI);
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
