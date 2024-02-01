package com.cool.request.utils.file;

import com.cool.request.utils.file.os.windows.WindowFileChooser;
import com.intellij.openapi.project.Project;

public class FileChooseUtils {
    private static BasicFileChooser osFileChooser;
    private static BasicFileChooser ideaFileChooser = new IdeaFileChooser();

    static {

        if (SystemOsUtils.isWindows()) {
            osFileChooser = new WindowFileChooser();
        }
        if (SystemOsUtils.isMacOs()) {
            osFileChooser = new MacFileChooser();
        }
        if (SystemOsUtils.isLinux()) {
            osFileChooser = new LinuxFileChooser();
        }

    }

    public static String chooseSingleFile(Project project, String basePath, String fileName) {
        try {
            if (osFileChooser != null) return osFileChooser.chooseSingleFile(basePath, fileName, project);
        } catch (Exception ignored) {
        }
        return ideaFileChooser.chooseSingleFile(basePath, fileName, project);
    }
    public static String chooseDirectory(Project project) {
        try {
            if (osFileChooser != null) return osFileChooser.chooseDirector(project);
        } catch (Exception ignored) {
        }
        return ideaFileChooser.chooseDirector( project);
    }

    public static String chooseFileSavePath(String basePath, String fileName, Project project) {
        try {
            if (osFileChooser != null) return osFileChooser.chooseFileSavePath(basePath, fileName, project);
        } catch (Exception ignored) {
        }
        return ideaFileChooser.chooseFileSavePath(basePath, fileName, project);
    }
}
