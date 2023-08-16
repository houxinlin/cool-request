package com.hxl.plugin.springboot.invoke.view.page;

import com.hxl.plugin.springboot.invoke.view.MultilingualEditor;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;

import javax.swing.*;

public class HTTPResponseView  extends  BasicEditPage {
    public HTTPResponseView(Project project) {
        super(project);
    }

    @Override
    public FileType getFileType() {
        return MultilingualEditor.JSON_FILE_TYPE;
    }
}
