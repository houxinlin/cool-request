/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * ApipostConfigurable.java is part of Cool Request
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

package com.cool.request.plugin.apipost;

import com.cool.request.common.state.ThirdPartyPersistent;
import com.intellij.openapi.options.ConfigurableBase;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class ApipostConfigurable extends ConfigurableBase<ApipostConfigUI, ApipostSetting> {
    private final Project project;

    public ApipostConfigurable(Project project,
                               @NonNls @NotNull String id,
                               @NotNull String displayName,
                               @NonNls @Nullable String helpTopic) {
        super(id, displayName, helpTopic);
        this.project = project;

    }

    @Override
    protected @NotNull ApipostSetting getSettings() {
        ThirdPartyPersistent.State settingPersistentState = ThirdPartyPersistent.getInstance();
        return new ApipostSetting(settingPersistentState.apipostHost, settingPersistentState.apipostToken);
    }

    @Override
    protected ApipostConfigUI createUi() {
        return new ApipostConfigUI(project);
    }
}
