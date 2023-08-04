package com.hxl.plugin.springboot.invoke.view.page;

import com.hxl.plugin.springboot.invoke.view.MultilingualEditor;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;

public class RawParamRequestBodyPage extends BasicEditPage {
    @Override
    public FileType getFileType() {
        return MultilingualEditor.TEXT_FILE_TYPE;
    }
    public RawParamRequestBodyPage(Project project) {
        super(project);
    }
}