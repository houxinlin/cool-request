package com.hxl.plugin.springboot.invoke.action;

import com.hxl.plugin.springboot.invoke.model.RequestMappingModel;
import com.hxl.plugin.springboot.invoke.openapi.OpenApiUtils;
import com.hxl.plugin.springboot.invoke.utils.ProjectUtils;
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

public class OpenApiAnAction extends AnAction {
    private MainTopTreeView mainTopTreeView;

    public OpenApiAnAction(MainTopTreeView mainTopTreeView) {
        super("Openapi", "Openapi", MyIcons.OPENAPI);
        this.mainTopTreeView = mainTopTreeView;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        String storagePath = FileChooseUtils.getSavePath(null, ProjectUtils.getCurrentProject().getName() + ".json", e.getProject());
        if (storagePath != null) {
            List<RequestMappingModel> selectRequestMappings = mainTopTreeView.getSelectRequestMappings();
            try {
                Files.write(Paths.get(storagePath), OpenApiUtils.toOpenApiJson(selectRequestMappings).getBytes(StandardCharsets.UTF_8));
            } catch (IOException ignored) {
            }
        }
    }
}
