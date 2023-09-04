package com.hxl.plugin.springboot.invoke.view.dialog;

import com.intellij.ide.actions.ShowSettingsUtilImpl;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurableGroup;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.ex.SortedConfigurableGroup;
import com.intellij.openapi.options.newEditor.SettingsDialog;
import com.intellij.openapi.options.newEditor.SettingsEditor;
import com.intellij.openapi.options.newEditor.SettingsFilter;
import com.intellij.openapi.options.newEditor.SettingsTreeView;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.NlsContexts;
import invoke.dsl.ApifoxConfiburable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;

public class SettingDialog   {
    public static void show(){
        Project openProject = ProjectManager.getInstance().getOpenProjects()[0];
        SortedConfigurableGroup sortedConfigurableGroup = new SortedConfigurableGroup("apifox", "apifox", "apifox", "apifox", 1) {
            @Override
            protected Configurable[] buildConfigurables() {
                return new Configurable[]{new ApifoxConfiburable()};
            }
        };
        ShowSettingsUtilImpl.getDialog(openProject, Arrays.asList(sortedConfigurableGroup), null).show();
    }
}