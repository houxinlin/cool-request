package com.cool.request.view.page;

import com.cool.request.view.MultilingualEditor;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;

public class HTTPResponseHeaderView  extends BasicEditPage {
    public HTTPResponseHeaderView(Project project) {
        super(project);
    }

    @Override
    public FileType getFileType() {
        return MultilingualEditor.TEXT_FILE_TYPE;
    }
}
