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