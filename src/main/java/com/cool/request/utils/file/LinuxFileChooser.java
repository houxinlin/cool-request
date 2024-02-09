package com.cool.request.utils.file;

import com.intellij.openapi.project.Project;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.MessageFormat;

/**
 * @author zhangpengjun
 * @date 2024/1/12
 */
public class LinuxFileChooser extends BasicFileChooser {
    /**
     * 在这里添加更多的选择命令，越多越好
     */
    enum Command {
        FILE_SELECT_K_DIALOG("kdialog", "--getopenfilename \"{0}\""),

        FILE_SAVE_K_DIALOG("kdialog", "--getsavefilename \"{0}\"");

        final String command;
        final String arg;

        Command(String command, String arg) {
            this.command = command;
            this.arg = arg;
        }

        public String formatArgs(String... args) {
            return MessageFormat.format(arg, (Object) args);
        }

        public String generatorCommand(String... args) {
            return command + " " + formatArgs(args);
        }
    }


    @Override
    public String chooseSingleFile(String basePath, String fileName, Project project) {
        return choose(basePath, fileName, true);
    }

    @Override
    public String chooseDirector(Project project) {
        throw new NullPointerException();
    }

    @Override
    public String chooseFileSavePath(String basePath, String fileName, Project project) {
        return choose(basePath, fileName, false);
    }

    private String choose(String basePath, String fileName, boolean open) {
        for (Command value : Command.values()) {
            if (value.toString().startsWith(open ? "FILE_SELECT" : "FILE_SAVE")) {
                try {
                    Process process = Runtime.getRuntime().exec(value.generatorCommand(basePath == null ? "" : basePath, fileName));
                    process.waitFor();
                    if (process.exitValue() == 0) {
                        BufferedReader errorOutputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                        return errorOutputReader.readLine();
                    }
                } catch (Exception ignored) {
                    throw new IllegalArgumentException("");

                }
            }
        }
        return null;

    }

}
