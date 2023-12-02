package com.hxl.plugin.springboot.invoke.utils.file;


import com.intellij.openapi.project.Project;

public abstract class BasicFileChooser {
    public abstract  String getFile(Project project);

    public abstract String getStoragePath(Project project);

    public abstract String getSavePath(String basePath, String fileName, Project project);
}
