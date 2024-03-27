/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * IdeaFileChooser.java is part of Cool Request
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

import com.intellij.openapi.fileChooser.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileWrapper;

import java.nio.file.Paths;

public class IdeaFileChooser extends BasicFileChooser {
    @Override
    public String chooseSingleFile(String basePath, String fileName, Project project) {
        FileChooserDescriptor descriptor = new FileChooserDescriptor(true, false, false,
                false, false, false);
        VirtualFile selectedFile = FileChooser.chooseFile(descriptor, project, null);
        if (selectedFile != null) {
            return selectedFile.getPath();
        }
        return null;
    }

    @Override
    public String chooseDirector(Project project) {
        FileChooserDescriptor descriptor = new FileChooserDescriptor(false, true, false,
                false, false, false);
        VirtualFile selectedFile = FileChooser.chooseFile(descriptor, project, null);
        if (selectedFile != null) {
            return selectedFile.getPath();
        }
        return null;
    }

    @Override
    public String chooseFileSavePath(String basePath, String fileName, Project project) {
        FileSaverDescriptor descriptor = new FileSaverDescriptor("Save As", "Choose a file name");
        FileSaverDialog saveFileDialog = FileChooserFactory.getInstance().createSaveFileDialog(descriptor, project);
        VirtualFileWrapper virtualFileWrapper = saveFileDialog.save((VirtualFile) null, fileName);
        if (virtualFileWrapper == null) return null;
        return virtualFileWrapper.getFile().toString();
    }
}
