package com.cool.request.plugin.apipost;

import com.cool.request.common.state.ThirdPartyPersistent;
import com.intellij.openapi.options.ConfigurableBase;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class ApipostConfigurable extends ConfigurableBase<ApipostConfigUI, ApipostSetting> {
    private final Project project;

    public ApipostConfigurable(Project project,
                               @NonNls @NotNull String id,
                               @NotNull String displayName,
                               @NonNls @Nullable String helpTopic) {
        super(id, displayName, helpTopic);
        this.project = project;

    }

    @Override
    protected @NotNull ApipostSetting getSettings() {
        ThirdPartyPersistent.State settingPersistentState = ThirdPartyPersistent.getInstance();
        return new ApipostSetting(settingPersistentState.apipostHost, settingPersistentState.apipostToken);
    }

    @Override
    protected ApipostConfigUI createUi() {
        return new ApipostConfigUI(project);
    }
}
