package com.cool.request.view.dialog;

import com.cool.request.plugin.apifox.ApifoxConfigurable;
import com.cool.request.plugin.apipost.ApipostConfigurable;
import com.cool.request.ui.dsl.CoolRequestSettingConfigurable;
import com.cool.request.ui.dsl.TraceSettingConfigurable;
import com.intellij.ide.actions.ShowSettingsUtilImpl;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurableGroup;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.List;

public class SettingDialog {
    public static Configurable[] createNewConfigurable(Project project) {
        return new Configurable[]{new CoolRequestSettingConfigurable(project),
                new TraceSettingConfigurable(project),
                new ApipostConfigurable(project, "cool.request.config.api-post", "Apipost", "api-post"),
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