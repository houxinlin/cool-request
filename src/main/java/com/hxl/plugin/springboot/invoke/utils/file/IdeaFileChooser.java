package com.hxl.plugin.springboot.invoke.utils.file;

import com.hxl.plugin.springboot.invoke.plugin.apifox.ApifoxProject;
import com.intellij.openapi.fileChooser.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileWrapper;

import java.nio.file.Paths;

public class IdeaFileChooser extends BasicFileChooser {
    @Override
    public String getFile() {
        Project[] currentProject = ProjectManager.getInstance().getOpenProjects();
        if (currentProject.length >= 1) {
            FileChooserDescriptor descriptor = new FileChooserDescriptor(true, false, false, false, false, false);
            VirtualFile selectedFile = FileChooser.chooseFile(descriptor, currentProject[0], null);
            if (selectedFile != null) {
                return selectedFile.getPath();
            }
        }
        return null;
    }

    @Override
    public String getStoragePath() {
        Project[] currentProject = ProjectManager.getInstance().getOpenProjects();
        FileChooserDescriptor descriptor = new FileChooserDescriptor(false, true, false, false, false, false);
        VirtualFile selectedFile = FileChooser.chooseFile(descriptor, currentProject[0], null);
        if (selectedFile != null) {
            return selectedFile.getPath();
        }
        return null;
    }

    @Override
    public String getSavePath(String basePath, String fileName, Project project) {
        FileSaverDescriptor descriptor = new FileSaverDescriptor("Save As", "Choose a file name");
        FileSaverDialog saveFileDialog = FileChooserFactory.getInstance().createSaveFileDialog(descriptor, project);
        VirtualFileWrapper virtualFileWrapper = saveFileDialog.save(basePath != null ? Paths.get(basePath) : null, fileName);
        if (virtualFileWrapper == null) return null;
        return virtualFileWrapper.getFile().toString();
    }
}
