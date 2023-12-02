package com.hxl.plugin.springboot.invoke.view.dialog;

import com.intellij.ide.actions.ShowSettingsUtilImpl;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ex.SortedConfigurableGroup;
import com.intellij.openapi.project.Project;
import invoke.dsl.ApifoxConfigurable;

import java.util.List;

public class SettingDialog   {
    public static void show(Project project){
        SortedConfigurableGroup apifox = new SortedConfigurableGroup("apifox", "apifox", "apifox", "apifox", 1) {
            @Override
            protected Configurable[] buildConfigurables() {
                return new Configurable[]{new ApifoxConfigurable(project)};
            }
        };
//        SortedConfigurableGroup apipost = new SortedConfigurableGroup("apipost", "apipost", "apipost", "apipost", 1) {
//            @Override
//            protected Configurable[] buildConfigurables() {
//                return new Configurable[]{new ApipostConfigurable()};
//            }
//        };
//        SortedConfigurableGroup download = new SortedConfigurableGroup("download", "下载", "下载", "下载", 1) {
//            @Override
//            protected Configurable[] buildConfigurables() {
//                return new Configurable[]{new DownloadConfigurable()};
//            }
//        };
        ShowSettingsUtilImpl.getDialog(project, List.of(apifox), null).show();
    }
}