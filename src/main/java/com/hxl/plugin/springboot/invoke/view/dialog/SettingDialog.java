package com.hxl.plugin.springboot.invoke.view.dialog;

import com.intellij.ide.actions.ShowSettingsUtilImpl;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurableGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsContexts;
import invoke.dsl.ApifoxConfigurable;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SettingDialog {
    public static void show(Project project) {
        Configurable[] configurables = {new ApifoxConfigurable(project)};
        CoolConfigurableGroup coolConfigurableGroup = new CoolConfigurableGroup(configurables);
        ShowSettingsUtilImpl.getDialog(project, List.of(coolConfigurableGroup), configurables[0]).show();
    }

    static class CoolConfigurableGroup implements ConfigurableGroup {
        private final Configurable[] configurables;

        public CoolConfigurableGroup(Configurable[] configurables) {
            this.configurables = configurables;
        }

        @Override
        public @NlsContexts.ConfigurableName String getDisplayName() {
            return "apifox";
        }

        @Override
        public Configurable @NotNull [] getConfigurables() {
            return configurables;
        }
    }
}