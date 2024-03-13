package com.cool.request.view.page;

import com.cool.request.view.MultilingualEditor;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;

public class BasicJSONRequestBodyPage extends BasicEditPage {
    public BasicJSONRequestBodyPage(Project project) {
        super(project);
    }

    @Override
    public FileType getFileType() {
        return MultilingualEditor.JSON_FILE_TYPE;
    }
}