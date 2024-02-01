package com.cool.request.view.dialog;

import com.cool.request.common.state.SettingPersistentState;
import com.intellij.openapi.options.ConfigurableBase;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsContexts;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class ApifoxConfigurable extends ConfigurableBase<ApiFox, ApifoxSetting> {
    private Project project;

    protected ApifoxConfigurable(Project project,
                                 @NonNls @NotNull String id,
                                 @NotNull @NlsContexts.ConfigurableName String displayName,
                                 @NonNls @Nullable String helpTopic) {
        super(id, displayName, helpTopic);
        this.project=project;


    }

    @Override
    protected @NotNull ApifoxSetting getSettings() {
        SettingPersistentState settingPersistentState = SettingPersistentState.getInstance();
        return new ApifoxSetting(settingPersistentState.getState().apiFoxAuthorization, settingPersistentState.getState().openApiToken);
    }

    @Override
    protected ApiFox createUi() {
        return new ApiFox(project);
    }
}
