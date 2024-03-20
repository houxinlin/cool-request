package com.cool.request.common.state;

import com.cool.request.common.bean.components.Component;
import com.cool.request.components.ComponentType;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service()
@State(name = "MarkPersistentV2", storages = @Storage("MarkPersistentV2.xml"))
public final class MarkPersistent implements PersistentStateComponent<MarkPersistent.State> {
    private MarkPersistent.State myState = new MarkPersistent.State();

    public static MarkPersistent getInstance(Project project) {
        return project.getService(MarkPersistent.class);
    }

    @Override
    public @NotNull MarkPersistent.State getState() {
        return myState;
    }

    @Override
    public void loadState(@NotNull State state) {
        this.myState = state;
    }

    public boolean in(Component component) {
        State state = getState();
        return state.getMarkComponentMap().getOrDefault(component.getComponentType(), new HashSet<>()).contains(component.getId());
    }

    public static class State {
        private Map<ComponentType, Set<String>> markComponentMap = new HashMap<>();

        public Map<ComponentType, Set<String>> getMarkComponentMap() {
            return markComponentMap;
        }

        public void setMarkComponentMap(Map<ComponentType, Set<String>> markComponentMap) {
            this.markComponentMap = markComponentMap;
        }
    }
}


