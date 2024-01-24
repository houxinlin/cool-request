package com.cool.request.state.project;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


@Service()
@State(name = "ProjectConfigPersistentComponent", storages = @Storage("ProjectConfigPersistentComponent.xml"))

public final class ProjectConfigPersistentComponent implements PersistentStateComponent<ProjectConfigPersistentComponent.State> {
    public static class State implements Serializable {
        public Map<String, String> projectEnvironmentMap;

        public State() {
            projectEnvironmentMap = new HashMap<>();
        }
    }

    private State myState = new State();

    @Override
    public State getState() {
        return myState;
    }

    @Override
    public void loadState(@NotNull State state) {
        myState = state;
        if (myState.projectEnvironmentMap == null) myState.projectEnvironmentMap = new HashMap<>();
    }

    public static State getInstance() {
        return ApplicationManager.getApplication().getService(ProjectConfigPersistentComponent.class).getState();
    }

}