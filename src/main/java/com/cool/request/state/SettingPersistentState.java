package com.cool.request.state;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.*;
import org.jetbrains.annotations.NotNull;

@State(name = "SpringMVCInvokeState", storages = @Storage("spring.invoke.state.xml"))
@Service()
public final class SettingPersistentState implements PersistentStateComponent<SettingsState> {
    public static SettingPersistentState getInstance() {
        return ApplicationManager.getApplication().getService(SettingPersistentState.class);
    }

    public SettingPersistentState() {
        state= new SettingsState();
    }

    private SettingsState state ;
    @Override
    public @NotNull SettingsState getState() {
        return state;
    }
    @Override
    public void loadState(@NotNull SettingsState state) {
        this.state=state;

    }
}