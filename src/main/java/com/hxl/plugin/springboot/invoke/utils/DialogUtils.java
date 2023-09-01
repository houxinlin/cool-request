package com.hxl.plugin.springboot.invoke.utils;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;

public class DialogUtils {
    public static void showSettingDialog() {
        Project openProject = ProjectManager.getInstance().getOpenProjects()[0];
        ShowSettingsUtil.getInstance().showSettingsDialog(openProject, new SettingConfig());
    }
}
