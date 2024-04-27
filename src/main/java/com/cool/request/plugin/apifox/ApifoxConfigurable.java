/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * ApifoxConfigurable.java is part of Cool Request
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

package com.cool.request.plugin.apifox;

import com.cool.request.common.state.SettingPersistentState;
import com.cool.request.view.dialog.ApiFox;
import com.intellij.openapi.options.ConfigurableBase;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsContexts;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class ApifoxConfigurable extends ConfigurableBase<ApiFox, ApifoxSetting> {
    private Project project;
    public ApifoxConfigurable(Project project,
                                 @NonNls @NotNull String id,
                                 @NotNull @NlsContexts.ConfigurableName String displayName,
                                 @NonNls @Nullable String helpTopic) {
        super(id, displayName, helpTopic);
        this.project=project;
    }

    @Override
    protected @NotNull ApifoxSetting getSettings() {
        SettingPersistentState settingPersistentState = SettingPersistentState.getInstance();
        return new ApifoxSetting(settingPersistentState.getState().apiFoxAuthorization, settingPersistentState.getState().openApiToken);
    }

    @Override
    protected ApiFox createUi() {
        return new ApiFox(project);
    }
}
