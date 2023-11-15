package com.hxl.plugin.springboot.invoke.utils.file;


import com.intellij.openapi.project.Project;

public abstract class BasicFileChooser {
    public abstract  String getFile();

    public abstract String getStoragePath();

    public abstract String getSavePath(String basePath, String fileName, Project project);
}
