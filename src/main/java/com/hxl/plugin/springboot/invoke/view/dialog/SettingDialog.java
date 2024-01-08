package com.hxl.plugin.springboot.invoke.view.dialog;

import com.intellij.ide.actions.ShowSettingsUtilImpl;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurableGroup;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SettingDialog {
    public static void show(Project project) {
        Configurable[] configurables = {
                new BaseSettingConfigurable(project, "cool.request.config.base", "Base", "base"),
                new ApifoxConfigurable(project, "cool.request.config.api-fox", "Apifox", "api-fox")
        };
        CoolConfigurableGroup coolConfigurableGroup = new CoolConfigurableGroup(configurables);
        ShowSettingsUtilImpl.getDialog(project, List.of(coolConfigurableGroup), configurables[0]).show();
    }

    static class CoolConfigurableGroup implements ConfigurableGroup {
        private final Configurable[] configurables;

        public CoolConfigurableGroup(Configurable[] configurables) {
            this.configurables = configurables;
        }

        @Override
        public String getDisplayName() {
            return "Apifox";
        }

        @Override
        public Configurable @NotNull [] getConfigurables() {
            return configurables;
        }
    }
}