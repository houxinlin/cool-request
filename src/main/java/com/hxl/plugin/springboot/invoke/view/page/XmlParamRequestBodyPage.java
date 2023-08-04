package com.hxl.plugin.springboot.invoke.view.page;

import com.hxl.plugin.springboot.invoke.view.MultilingualEditor;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;

public class XmlParamRequestBodyPage extends BasicEditPage {
    @Override
    public FileType getFileType() {
        return MultilingualEditor.XML_FILE_TYPE;
    }
    public XmlParamRequestBodyPage(Project project) {
        super(project);
    }
}
