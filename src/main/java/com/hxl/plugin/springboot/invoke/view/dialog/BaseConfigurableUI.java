package com.hxl.plugin.springboot.invoke.view.dialog;

import com.hxl.plugin.springboot.invoke.IdeaTopic;
import com.hxl.plugin.springboot.invoke.state.SettingPersistentState;
import com.hxl.plugin.springboot.invoke.state.SettingsState;
import com.hxl.plugin.springboot.invoke.utils.ResourceBundleUtils;
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
    private Project project;

    public BaseConfigurableUI(Project project) {
        this.project = project;
    }

    @Override
    public void reset(@NotNull BaseSetting settings) {
        languageValue.setSelectedIndex(settings.getLanguageIndex());
        autoNavigationCheck.setText(ResourceBundleUtils.getString("auto.goto.code"));
        autoNavigationDesc.setText(ResourceBundleUtils.getString("auto.goto.code.desc"));

        gatewayCheck.setText(ResourceBundleUtils.getString("listener.gateway"));
        listenerGatewayDesc.setText(ResourceBundleUtils.getString("listener.gateway.desc"));

        autoRefreshCheck.setText(ResourceBundleUtils.getString("auto.refresh.component"));
        autoRefreshDesc.setText(ResourceBundleUtils.getString("auto.refresh.component.desc"));

        autoRefreshCheck.setSelected(settings.isAutoRefreshData());
        autoNavigationCheck.setSelected(settings.isAutoNavigation());
        gatewayCheck.setSelected(settings.isListenerGateway());

    }

    @Override
    public boolean isModified(@NotNull BaseSetting settings) {
        return settings.getLanguageIndex() != languageValue.getSelectedIndex() ||
                settings.isAutoRefreshData() != autoRefreshCheck.isSelected() ||
                settings.isListenerGateway() != gatewayCheck.isSelected() ||
                settings.isAutoNavigation() != autoNavigationCheck.isSelected();
    }

    @Override
    public void apply(@NotNull BaseSetting settings) throws ConfigurationException {
        settings.setLanguageIndex(languageValue.getSelectedIndex());
        settings.setAutoRefreshData(autoRefreshCheck.isSelected());
        settings.setListenerGateway(gatewayCheck.isSelected());
        settings.setAutoNavigation(autoNavigationCheck.isSelected());

        SettingsState state = SettingPersistentState.getInstance().getState();
        state.languageValue = languageValue.getSelectedIndex();
        state.autoNavigation = settings.isAutoNavigation();
        state.listenerGateway = settings.isListenerGateway();
        state.autoRefreshData = settings.isAutoRefreshData();
        reset(settings);
        ApplicationManager.getApplication().getMessageBus().syncPublisher(IdeaTopic.LANGUAGE_CHANGE).event();
    }

    @Override
    public @NotNull JComponent getComponent() {
        return root;
    }
}
