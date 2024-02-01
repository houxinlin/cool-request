package com.cool.request.common.state;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NotNull;

@State(name = "CoolRequestSetting", storages = @Storage("CoolRequestSetting.xml"))
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