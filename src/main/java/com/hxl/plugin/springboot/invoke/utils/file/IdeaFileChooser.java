package com.hxl.plugin.springboot.invoke.utils.file;

import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.vfs.VirtualFile;

public class IdeaFileChooser  extends BasicFileChooser{
    @Override
    public String getFile() {
        Project[] currentProject = ProjectManager.getInstance().getOpenProjects();
        if (currentProject.length>=1){
            FileChooserDescriptor descriptor = new FileChooserDescriptor(true, false, false, false, false, false);
            VirtualFile selectedFile = FileChooser.chooseFile(descriptor,currentProject[0] , null);
            if (selectedFile != null) {
                return selectedFile.getPath();
            }
        }
        return null;
    }
}
