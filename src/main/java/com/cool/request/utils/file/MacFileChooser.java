/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * MacFileChooser.java is part of Cool Request
 *
 * License: GPL-3.0+
 *
 * Cool Request is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Cool Request is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Cool Request.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.cool.request.utils.file;

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
        throw new NullPointerException();
//        return chooseFileInternal(null, null, true);
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
