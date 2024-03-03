package com.cool.request.view.editor;

import com.cool.request.common.bean.components.controller.Controller;
import com.cool.request.utils.HttpMethodIconUtils;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypes;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class CoolRequestFileType implements FileType {
    private final Controller controller;

    public CoolRequestFileType(Controller controller) {
        this.controller = controller;
    }

    @Override
    public @NonNls @NotNull String getName() {
        return FileTypes.UNKNOWN.getName();
    }

    @Override
    public @NotNull String getDescription() {
        return "coolrequest";
    }

    @Override
    public @NotNull String getDefaultExtension() {
        return "coolrequest";
    }

    @Override
    public Icon getIcon() {
        return HttpMethodIconUtils.getIconByHttpMethod(controller.getHttpMethod());
    }

    @Override
    public boolean isBinary() {
        return true;
    }
}
