package com.cool.request.view.dialog;

import com.cool.request.common.state.SettingPersistentState;
import com.cool.request.common.state.SettingsState;
import com.cool.request.common.constant.CoolRequestIdeaTopic;
import com.cool.request.utils.ResourceBundleUtils;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.options.ConfigurableUi;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class BaseConfigurableUI implements ConfigurableUi<BaseSetting> {
    private JPanel root;
    private JComboBox languageValue;
    private JLabel language;
    private JCheckBox autoNavigationCheck;
    private JCheckBox gatewayCheck;
    private JCheckBox autoRefreshCheck;
    private JLabel autoNavigationDesc;
    private JLabel listenerGatewayDesc;
    private JLabel autoRefreshDesc;
    private JCheckBox enableDynamicRefreshCheckbox;
    private JLabel enableDynamicRefreshDesc;
    private JCheckBox mergeApiAndRequestCheckbox;
    private Project project;

    public BaseConfigurableUI(Project project) {
        this.project = project;
    }

    @Override
    public void reset(@NotNull BaseSetting settings) {
        languageValue.setSelectedIndex(settings.getLanguageIndex());
        enableDynamicRefreshCheckbox.setText(ResourceBundleUtils.getString("enable.dynamic.refresh"));
        enableDynamicRefreshDesc.setText(ResourceBundleUtils.getString("enable.dynamic.refresh.desc"));

        autoNavigationCheck.setText(ResourceBundleUtils.getString("auto.goto.code"));
        autoNavigationDesc.setText(ResourceBundleUtils.getString("auto.goto.code.desc"));

        gatewayCheck.setText(ResourceBundleUtils.getString("listener.gateway"));
        listenerGatewayDesc.setText(ResourceBundleUtils.getString("listener.gateway.desc"));

        autoRefreshCheck.setText(ResourceBundleUtils.getString("auto.refresh.component"));
        autoRefreshDesc.setText(ResourceBundleUtils.getString("auto.refresh.component.desc"));

        autoRefreshCheck.setSelected(settings.isAutoRefreshData());
        autoNavigationCheck.setSelected(settings.isAutoNavigation());
        gatewayCheck.setSelected(settings.isListenerGateway());
        enableDynamicRefreshCheckbox.setSelected(settings.isEnableDynamicRefresh());
        mergeApiAndRequestCheckbox.setSelected(settings.isMergeApiAndRequest());
        mergeApiAndRequestCheckbox.setText(ResourceBundleUtils.getString("merge.api.request.ui"));
    }

    @Override
    public boolean isModified(@NotNull BaseSetting settings) {
        return settings.getLanguageIndex() != languageValue.getSelectedIndex() ||
                settings.isAutoRefreshData() != autoRefreshCheck.isSelected() ||
                settings.isListenerGateway() != gatewayCheck.isSelected() ||
                settings.isEnableDynamicRefresh() != enableDynamicRefreshCheckbox.isSelected() ||
                settings.isMergeApiAndRequest() != mergeApiAndRequestCheckbox.isSelected() ||
                settings.isAutoNavigation() != autoNavigationCheck.isSelected();
    }

    @Override
    public void apply(@NotNull BaseSetting settings) throws ConfigurationException {
        settings.setLanguageIndex(languageValue.getSelectedIndex());
        settings.setAutoRefreshData(autoRefreshCheck.isSelected());
        settings.setListenerGateway(gatewayCheck.isSelected());
        settings.setAutoNavigation(autoNavigationCheck.isSelected());
        settings.setEnableDynamicRefresh(enableDynamicRefreshCheckbox.isSelected());
        settings.setMergeApiAndRequest(mergeApiAndRequestCheckbox.isSelected());

        SettingsState state = SettingPersistentState.getInstance().getState();
        state.languageValue = languageValue.getSelectedIndex();
        state.autoNavigation = settings.isAutoNavigation();
        state.listenerGateway = settings.isListenerGateway();
        state.autoRefreshData = settings.isAutoRefreshData();
        state.enableDynamicRefresh = settings.isEnableDynamicRefresh();
        state.mergeApiAndRequest = settings.isMergeApiAndRequest();

        reset(settings);
        ApplicationManager.getApplication().getMessageBus().syncPublisher(CoolRequestIdeaTopic.COOL_REQUEST_SETTING_CHANGE).event();
    }

    @Override
    public @NotNull JComponent getComponent() {
        return root;
    }
}
