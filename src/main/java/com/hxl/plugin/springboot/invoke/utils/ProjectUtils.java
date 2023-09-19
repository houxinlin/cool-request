package com.hxl.plugin.springboot.invoke.utils;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;

public class ProjectUtils {
    public static Project getCurrentProject(){
        return ProjectManager.getInstance().getOpenProjects()[0];
    }
}
