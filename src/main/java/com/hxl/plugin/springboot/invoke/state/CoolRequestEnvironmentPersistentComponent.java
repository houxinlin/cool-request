package com.hxl.plugin.springboot.invoke.state;

import com.hxl.plugin.springboot.invoke.bean.RequestEnvironment;
import com.hxl.plugin.springboot.invoke.model.GatewayModel;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Service
@State(name = "CoolRequestEnvironmentPersistentComponent", storages = @Storage("CoolRequestEnvironmentPersistentComponent.xml"))
public final class CoolRequestEnvironmentPersistentComponent implements PersistentStateComponent<CoolRequestEnvironmentPersistentComponent.State> {

    public static class State implements Serializable {
        public List<RequestEnvironment> environments;

        public State() {
            environments = new ArrayList<>();
        }
    }

    private State myState = new State();

    public State getState() {
        return myState;
    }

    public void loadState(@NotNull State state) {
        myState = state;
        if (myState.environments == null) myState.environments = new ArrayList<>();
    }

    public static State getInstance() {
        return ApplicationManager.getApplication().getService(CoolRequestEnvironmentPersistentComponent.class).getState();
    }

}