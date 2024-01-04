package com.hxl.plugin.springboot.invoke.view.dialog;

import com.hxl.plugin.springboot.invoke.plugin.apifox.ApiFoxExport;
import com.hxl.plugin.springboot.invoke.plugin.apifox.ApiFoxExportCondition;
import com.hxl.plugin.springboot.invoke.state.SettingPersistentState;
import com.intellij.openapi.options.ConfigurableUi;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import icons.MyIcons;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

public class ApiFox implements ConfigurableUi<ApifoxSetting>, ActionListener {

    private JPanel root;
    private JTextField httpText;
    private JTextField tokenText;
    private JButton checkButton;
    private JLabel httpResult;
    private JLabel tokenResult;
    private ApiFoxExport apiFoxExport;

    private Project project;

    public ApiFox(Project project) {
        apiFoxExport = new ApiFoxExport(project);
        this.project = project;
    }

    @Override
    public void reset(@NotNull ApifoxSetting settings) {
        httpText.setText(settings.getHttpToken());
        tokenText.setText(settings.getApiToken());
    }

    @Override
    public boolean isModified(@NotNull ApifoxSetting settings) {
        return false;
    }

    @Override
    public void apply(@NotNull ApifoxSetting settings) throws ConfigurationException {
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        tokenResult.setText("");
        httpResult.setText("");
        ProgressManager.getInstance().run(new Task.Backgroundable(project, "Checking....") {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                ApiFoxExportCondition apiFoxExportCondition = new ApiFoxExportCondition(httpText.getText(), tokenText.getText());
                SettingPersistentState instance = SettingPersistentState.getInstance();
                instance.getState().apiFoxAuthorization = httpText.getText();
                instance.getState().openApiToken = tokenText.getText();

                Map<String, Boolean> result = apiFoxExport.checkToken(apiFoxExportCondition);
                SwingUtilities.invokeLater(() -> {
                    tokenResult.setText(result.getOrDefault(ApiFoxExportCondition.KEY_API_FOX_OPEN_AUTHORIZATION, false) ? "Success" : "Invalid Token");
                    httpResult.setText(result.getOrDefault(ApiFoxExportCondition.KEY_API_FOX_AUTHORIZATION, false) ? "Success" : "Invalid Token");
                });

            }
        });
    }

    @Override
    public @NotNull JComponent getComponent() {
        checkButton.addActionListener(this);
        return root;
    }

}
