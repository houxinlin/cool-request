package com.cool.request.view.dialog;

import com.cool.request.common.state.SettingPersistentState;
import com.cool.request.common.state.SettingsState;
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
        SettingsState state = SettingPersistentState.getInstance().getState();
        BaseSetting baseSetting = new BaseSetting(state.languageValue);
        baseSetting.setAutoNavigation(state.autoNavigation);
        baseSetting.setListenerGateway(state.listenerGateway);
        baseSetting.setAutoRefreshData(state.autoRefreshData);
        baseSetting.setEnableDynamicRefresh(state.enableDynamicRefresh);
        baseSetting.setMergeApiAndRequest(state.mergeApiAndRequest);
        baseSetting.setProxyIp(state.proxyIp);
        baseSetting.setParameterCoverage(state.parameterCoverage);
        baseSetting.setProxyPort(state.proxyPort);
        return baseSetting;
    }

    @Override
    protected BaseConfigurableUI createUi() {
        return new BaseConfigurableUI(project);
    }
}
