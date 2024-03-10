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
