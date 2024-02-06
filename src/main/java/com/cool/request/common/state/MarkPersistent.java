package com.cool.request.common.state;

import com.cool.request.common.bean.components.Component;
import com.cool.request.common.bean.components.controller.Controller;
import com.cool.request.common.bean.components.scheduled.SpringScheduled;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

@Service()
@State(name = "MarkPersistent", storages = @Storage("MarkPersistent.xml"))
public final class MarkPersistent implements PersistentStateComponent<MarkPersistent.State> {
    private MarkPersistent.State myState = new MarkPersistent.State();

    public static MarkPersistent getInstance(Project project) {
        return project.getService(MarkPersistent.class);
    }

    @Override
    public @Nullable MarkPersistent.State getState() {
        return myState;
    }

    @Override
    public void loadState(@NotNull State state) {
        this.myState = state;
    }

    public boolean in(Component component) {
        State state = getState();
        if (component instanceof Controller) {
            return state.getControllerMark().contains(component.getId());
        }
        if (component instanceof SpringScheduled) {
            return state.getScheduleMark().contains(component.getId());
        }
        return false;
    }

    public static class State {
        private Set<String> controllerMark = new HashSet<>();
        private Set<String> scheduleMark = new HashSet<>();

        public Set<String> getControllerMark() {
            return controllerMark;
        }

        public void setControllerMark(Set<String> controllerMark) {
            this.controllerMark = controllerMark;
        }

        public Set<String> getScheduleMark() {
            return scheduleMark;
        }

        public void setScheduleMark(Set<String> scheduleMark) {
            this.scheduleMark = scheduleMark;
        }
    }
}


