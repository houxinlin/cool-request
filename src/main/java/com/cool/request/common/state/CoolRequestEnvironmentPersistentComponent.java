/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * CoolRequestEnvironmentPersistentComponent.java is part of Cool Request
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

import com.cool.request.common.bean.RequestEnvironment;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.Converter;
import com.intellij.util.xmlb.annotations.OptionTag;
import com.intellij.util.xmlb.annotations.Tag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Service
@State(name = "CoolRequestEnvironmentPersistentComponent", storages  = @Storage("CoolRequestEnvironmentPersistentComponent.xml"))
public final class CoolRequestEnvironmentPersistentComponent implements PersistentStateComponent<CoolRequestEnvironmentPersistentComponent.State> {
    private State myState = new State();

    public static State getInstance(Project project) {
        return project.getService(CoolRequestEnvironmentPersistentComponent.class).getState();
    }

    @Override
    public State getState() {
        return myState;
    }


    @Override
    public void loadState(@NotNull State state) {
        myState = state;
    }

    public static class State implements Serializable {
        @OptionTag(converter = RequestEnvironmentConvert.class)
        private List<RequestEnvironment> environments = new ArrayList<>();
        @Tag
        private String selectId;

        public List<RequestEnvironment> getEnvironments() {
            return environments;
        }

        public void setEnvironments(List<RequestEnvironment> environments) {
            this.environments = environments;
        }

        public String getSelectId() {
            return selectId;
        }

        public void setSelectId(String selectId) {
            this.selectId = selectId;
        }


        public State() {
        }

        public void addNewEnv(String name) {
            RequestEnvironment requestEnvironment = new RequestEnvironment();
            requestEnvironment.setEnvironmentName(name);
            requestEnvironment.setId(UUID.randomUUID().toString());
            environments.add(requestEnvironment);
        }
    }

    public static class RequestEnvironmentConvert extends Converter<List<RequestEnvironment>> {
        @Override
        public @Nullable List<RequestEnvironment> fromString(@NotNull String value) {
            Gson gson = new Gson();
            return gson.fromJson(value, new TypeToken<List<RequestEnvironment>>() {
            }.getType());

        }

        @Override
        public @Nullable String toString(@NotNull List<RequestEnvironment> value) {
            return new Gson().toJson(value);
        }
    }

}