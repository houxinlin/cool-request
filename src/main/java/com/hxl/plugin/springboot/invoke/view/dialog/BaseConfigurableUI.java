package com.hxl.plugin.springboot.invoke.view.dialog;

import com.hxl.plugin.springboot.invoke.IdeaTopic;
import com.hxl.plugin.springboot.invoke.state.SettingPersistentState;
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
    private Project project;

    public BaseConfigurableUI(Project project) {
        this.project = project;
    }

    @Override
    public void reset(@NotNull BaseSetting settings) {
        languageValue.setSelectedIndex(settings.getLanguageIndex());
    }

    @Override
    public boolean isModified(@NotNull BaseSetting settings) {
        return settings.getLanguageIndex() != languageValue.getSelectedIndex();
    }

    @Override
    public void apply(@NotNull BaseSetting settings) throws ConfigurationException {
        settings.setLanguageIndex(languageValue.getSelectedIndex());
        SettingPersistentState.getInstance().getState().languageValue = languageValue.getSelectedIndex();
        ApplicationManager.getApplication().getMessageBus().syncPublisher(IdeaTopic.LANGUAGE_CHANGE).event();
    }

    @Override
    public @NotNull JComponent getComponent() {
        return root;
    }
}
