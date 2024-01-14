package com.hxl.plugin.springboot.invoke.utils.file;

import com.intellij.openapi.project.Project;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author zhangpengjun
 * @date 2024/1/12
 */
public class MacFileChooser extends BasicFileChooser {

    @Override
    public String chooseSingleFile(String basePath, String fileName, Project project) {
        return chooseFileInternal(basePath, fileName, false);
    }

    @Override
    public String chooseDirector(Project project) {
        return chooseFileInternal(null, null, true);
    }

    @Override
    public String chooseFileSavePath(String basePath, String fileName, Project project) {
        return chooseFileInternal(basePath, fileName, false);
    }

    private String chooseFileInternal(String basePath, String fileName, boolean isDirectory) {
        String script = isDirectory ? "choose folder" : "choose file";
        if (basePath != null) {
            script += " default location \"" + basePath + "\"";
        }
        if (fileName != null) {
            script += " default name \"" + fileName + "\"";
        }

        return executeAppleScript(script);
    }

    private String executeAppleScript(String script) {
        try {
            Process process = Runtime.getRuntime().exec(new String[]{"osascript", "-e", script});
            process.waitFor();
            if (process.exitValue() == 0) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                return reader.readLine();
            }
        } catch (IOException | InterruptedException ignored) {
        }

        throw new IllegalArgumentException("");
    }

}
