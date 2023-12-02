package com.hxl.plugin.springboot.invoke.view.page;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.PlainTextFileType;
import com.intellij.openapi.project.Project;

public class TextEditPage  extends BasicEditPage{
    public TextEditPage(Project project) {
        super(project);
    }

    @Override
    public FileType getFileType() {
        return PlainTextFileType.INSTANCE;
    }
}
