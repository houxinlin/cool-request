/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * StaticResourcePersistent.java is part of Cool Request
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

package com.cool.request.components.staticServer;

import com.cool.request.utils.GsonUtils;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.Converter;
import com.intellij.util.xmlb.annotations.OptionTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Service()
@State(name = "StaticResourcePersistent", storages =  @Storage("spring.invoke.StaticResourcePersistent.xml"))
public final class StaticResourcePersistent implements PersistentStateComponent<StaticResourcePersistent.State> {
    public static StaticResourcePersistent.State getInstance() {
        return ApplicationManager.getApplication().getService(StaticResourcePersistent.class).getState();
    }

    private State state = new State();

    @Override
    public @Nullable StaticResourcePersistent.State getState() {
        return state;
    }

    @Override
    public void loadState(@NotNull State state) {
        this.state = state;
    }

    public static class State implements Serializable {
        @OptionTag(converter = StaticServerConvert.class)
        private List<StaticServer> staticServers = new ArrayList<>();

        public List<StaticServer> getStaticServers() {
            return staticServers;
        }

        public void setStaticServers(List<StaticServer> staticServers) {
            this.staticServers = staticServers;
        }
    }

    public static class StaticServerConvert extends Converter<List<StaticServer>> {
        @Override
        public @Nullable List<StaticServer> fromString(@NotNull String value) {
            return new Gson().fromJson(value, new TypeToken<List<StaticServer>>() {
            }.getType());
        }

        @Override
        public @Nullable String toString(@NotNull List<StaticServer> value) {
            return GsonUtils.toJsonString(value);
        }
    }
}
