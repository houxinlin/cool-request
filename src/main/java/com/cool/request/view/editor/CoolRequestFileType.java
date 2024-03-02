package com.cool.request.view.editor;

import com.cool.request.common.bean.components.controller.Controller;
import com.cool.request.common.icons.CoolRequestIcons;
import com.intellij.openapi.fileTypes.FileType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class CoolRequestFileType implements FileType {
    private Controller controller;

    public CoolRequestFileType(Controller controller) {
        this.controller = controller;
    }

    @Override
    public @NonNls @NotNull String getName() {
        return controller.getUrl();
    }

    @Override
    public @NotNull String getDescription() {
        return "null";
    }

    @Override
    public @NotNull String getDefaultExtension() {
        return "coolrequest";
    }

    @Override
    public Icon getIcon() {
        return CoolRequestIcons.MAIN;
    }

    @Override
    public boolean isBinary() {
        return false;
    }
}
