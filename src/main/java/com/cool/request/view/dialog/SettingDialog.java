package com.cool.request.view.dialog;

import com.intellij.ide.actions.ShowSettingsUtilImpl;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurableGroup;
import com.intellij.openapi.project.Project;
import invoke.dsl.SettingConfigurable;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.List;

public class SettingDialog {
    public static Configurable[] createNewConfigurable(Project project) {
        return new Configurable[]{
                new SettingConfigurable(project),
                new ApifoxConfigurable(project, "cool.request.config.api-fox", "Apifox", "api-fox")
        };
    }

    public static void show(Project project) {
        show(project, createNewConfigurable(project), 0);
    }

    public static void show(Project project, Configurable[] configurables, int toSelect) {
        SwingUtilities.invokeLater(() -> {
            CoolConfigurableGroup coolConfigurableGroup = new CoolConfigurableGroup(configurables);
            ShowSettingsUtilImpl.getDialog(project, List.of(coolConfigurableGroup), configurables[toSelect]).show();
        });
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