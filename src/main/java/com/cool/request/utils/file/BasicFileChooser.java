package com.cool.request.utils.file;


import com.intellij.openapi.project.Project;

public abstract class BasicFileChooser {
    public abstract String chooseSingleFile(String basePath, String fileName,Project project);

    public abstract String chooseDirector(Project project);

    public abstract String chooseFileSavePath(String basePath, String fileName, Project project);
}
