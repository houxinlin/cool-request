package com.hxl.plugin.springboot.invoke.utils.file;

import com.intellij.openapi.project.Project;

public class FileChooseUtils {
    public static String getFile(Project project) {
//        try {
//            if (System.getProperty("os.name").toLowerCase().contains("win")) {
//                return new WindowFileChooser().getFile();
//            }
//        } catch (Exception ignored) {
//        }
        IdeaFileChooser ideaFileChooser = new IdeaFileChooser();
        return ideaFileChooser.getFile(project);
    }

    public static String getStoragePath(Project project){
        return  new IdeaFileChooser().getStoragePath(project);
    }
    public static String getSavePath(String basePath, String fileName, Project project){
        return  new IdeaFileChooser().getSavePath(basePath, fileName, project);
    }
}
