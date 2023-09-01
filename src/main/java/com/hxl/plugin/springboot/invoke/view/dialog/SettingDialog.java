package com.hxl.plugin.springboot.invoke.view.dialog;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurableGroup;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.newEditor.SettingsDialog;
import com.intellij.openapi.options.newEditor.SettingsEditor;
import com.intellij.openapi.options.newEditor.SettingsFilter;
import com.intellij.openapi.options.newEditor.SettingsTreeView;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.NlsContexts;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;

public class SettingDialog  extends SettingsDialog {
    public SettingDialog(Project project, String key) {
        super(project, key, new Config(), true, true);
    }
    //    @Override
//    protected @Nullable JComponent createCenterPanel() {
//        Project project = ProjectManager.getInstance().getOpenProjects()[0];
//
//        return  new SettingsEditor(myDisposable, project, groups, configurable, filter, this::treeViewFactory);
//    }
static class Config implements   Configurable{
        @Override
        public @NlsContexts.ConfigurableName String getDisplayName() {
            return "a";
        }

        @Override
        public @Nullable JComponent createComponent() {
            return new JButton("a");
        }

        @Override
        public boolean isModified() {
            return false;
        }

        @Override
        public void apply() throws ConfigurationException {

        }
    }
}
