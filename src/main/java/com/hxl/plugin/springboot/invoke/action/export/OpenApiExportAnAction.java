package com.hxl.plugin.springboot.invoke.action.export;

import com.hxl.plugin.springboot.invoke.bean.components.controller.Controller;
import com.hxl.plugin.springboot.invoke.openapi.OpenApiUtils;
import com.hxl.plugin.springboot.invoke.utils.file.FileChooseUtils;
import com.hxl.plugin.springboot.invoke.view.main.MainTopTreeView;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import icons.MyIcons;
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
