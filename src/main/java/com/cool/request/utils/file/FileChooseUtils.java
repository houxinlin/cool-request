/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * FileChooseUtils.java is part of Cool Request
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
