package com.cool.request.common.state;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

@State(name = "CoolRequestSetting", storages = @Storage("CoolRequestSetting.xml"))
@Service()
public final class SettingPersistentState implements PersistentStateComponent<SettingsState> {
    public static SettingPersistentState getInstance() {
        return ApplicationManager.getApplication().getService(SettingPersistentState.class);
    }
    private KeyStroke currentKeyStroke;
    public SettingPersistentState() {
        state= new SettingsState();
    }

    public KeyStroke getCurrentKeyStroke() {
        return currentKeyStroke;
    }

    public void setCurrentKeyStroke(KeyStroke currentKeyStroke) {
        this.currentKeyStroke = currentKeyStroke;
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