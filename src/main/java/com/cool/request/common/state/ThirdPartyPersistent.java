/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * ThirdPartyPersistent.java is part of Cool Request
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

package com.cool.request.common.state;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Service()
@State(name = "ThirdPartyPersistent", storages = @Storage("ThirdPartyPersistent.xml"))
public final class ThirdPartyPersistent implements PersistentStateComponent<ThirdPartyPersistent.State> {
    private ThirdPartyPersistent.State myState = new ThirdPartyPersistent.State();

    public static ThirdPartyPersistent.State getInstance() {
        return ApplicationManager.getApplication().getService(ThirdPartyPersistent.class).getState();
    }

    @Override
    public @Nullable ThirdPartyPersistent.State getState() {
        return myState;
    }

    @Override
    public void loadState(@NotNull State state) {
        myState = state;
    }

    public static class State {
        public String apipostHost = "https://sync-project-ide.apipost.cn";
        public String apipostToken = "";

    }
}
