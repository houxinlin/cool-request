package com.cool.request.component.http;

import com.cool.request.common.bean.RequestEnvironment;
import com.cool.request.utils.ObjectMappingUtils;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.*;
import com.intellij.util.xmlb.Converter;
import com.intellij.util.xmlb.annotations.CollectionBean;
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
            return ObjectMappingUtils.toJsonString(value);
        }
    }
}
