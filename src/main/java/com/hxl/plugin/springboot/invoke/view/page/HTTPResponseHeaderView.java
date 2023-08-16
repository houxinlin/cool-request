package com.hxl.plugin.springboot.invoke.view.page;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;

import javax.swing.*;

public class HTTPResponseHeaderView  extends BasicEditPage {
    public HTTPResponseHeaderView(Project project) {
        super(project);
    }

    @Override
    public FileType getFileType() {
        return null;
    }
}
