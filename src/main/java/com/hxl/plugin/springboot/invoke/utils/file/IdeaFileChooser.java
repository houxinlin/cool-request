package com.hxl.plugin.springboot.invoke.utils.file;

import com.intellij.openapi.fileChooser.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileWrapper;

import java.nio.file.Paths;

public class IdeaFileChooser extends BasicFileChooser {
    @Override
    public String chooseSingleFile(String basePath, String fileName, Project project) {
        FileChooserDescriptor descriptor = new FileChooserDescriptor(true, false, false, false, false, false);
        VirtualFile selectedFile = FileChooser.chooseFile(descriptor, project, null);
        if (selectedFile != null) {
            return selectedFile.getPath();
        }
        return null;
    }

    @Override
    public String chooseDirector(Project project) {
        FileChooserDescriptor descriptor = new FileChooserDescriptor(false, true, false, false, false, false);
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
        VirtualFileWrapper virtualFileWrapper = saveFileDialog.save(basePath != null ? Paths.get(basePath) : null, fileName);
        if (virtualFileWrapper == null) return null;
        return virtualFileWrapper.getFile().toString();
    }
}
