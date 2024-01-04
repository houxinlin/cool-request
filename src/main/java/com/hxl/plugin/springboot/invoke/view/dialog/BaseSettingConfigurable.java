package com.hxl.plugin.springboot.invoke.view.dialog;

import com.hxl.plugin.springboot.invoke.state.SettingPersistentState;
import com.intellij.openapi.options.ConfigurableBase;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsContexts;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BaseSettingConfigurable extends ConfigurableBase<BaseConfigurableUI, BaseSetting> {
    private Project project;

    public BaseSettingConfigurable(Project project,
                                   @NonNls @NotNull String id,
                                   @NotNull @NlsContexts.ConfigurableName String displayName,
                                   @NonNls @Nullable String helpTopic) {
        super(id, displayName, helpTopic);
        this.project = project;
    }

    @Override
    protected @NotNull BaseSetting getSettings() {
        return new BaseSetting(SettingPersistentState.getInstance().getState().languageValue);
    }

    @Override
    protected BaseConfigurableUI createUi() {
        return new BaseConfigurableUI(project);
    }
}
