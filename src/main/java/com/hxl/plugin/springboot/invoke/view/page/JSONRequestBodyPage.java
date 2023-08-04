package com.hxl.plugin.springboot.invoke.view.page;

import com.hxl.plugin.springboot.invoke.view.MultilingualEditor;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;

public class JSONRequestBodyPage extends BasicEditPage {
    @Override
    public FileType getFileType() {
        return MultilingualEditor.JSON_FILE_TYPE;
    }
    public JSONRequestBodyPage(Project project) {
        super(project);
    }
}
