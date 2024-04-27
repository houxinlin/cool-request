/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * SettingDialog.java is part of Cool Request
 *
 * License: GPL-3.0+
 *
 * Cool Request is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Cool Request is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Cool Request.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.cool.request.view.dialog;

import com.cool.request.plugin.apifox.ApifoxConfigurable;
import com.cool.request.plugin.apipost.ApipostConfigurable;
import com.cool.request.ui.dsl.CoolRequestSettingConfigurable;
import com.cool.request.ui.dsl.TraceSettingConfigurable;
import com.intellij.ide.actions.ShowSettingsUtilImpl;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurableGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.List;

public class SettingDialog {
    public static Configurable[] createNewConfigurable(Project project) {
        return new Configurable[]{
                new CoolRequestSettingConfigurable(project),
                new TraceSettingConfigurable(project),
                new ApipostConfigurable(project, "cool.request.config.api-post", "Apipost", "api-post"),
                new ApifoxConfigurable(project, "cool.request.config.api-fox", "Apifox", "api-fox")
        };
    }

    public static void show(Project project) {

        show(project, createNewConfigurable(project), 0);
    }

    public static void show(Project project, Configurable[] configurables, int toSelect) {
        CoolConfigurableGroup coolConfigurableGroup = new CoolConfigurableGroup(configurables);
        SwingUtilities.invokeLater(() -> {
            DialogWrapper dialogWrapper = ShowSettingsUtilImpl.getDialog(project, List.of(coolConfigurableGroup), configurables[toSelect]);
            dialogWrapper.show();
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