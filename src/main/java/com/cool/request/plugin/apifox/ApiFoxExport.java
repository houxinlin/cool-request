package com.cool.request.plugin.apifox;

import com.cool.request.common.state.SettingPersistentState;
import com.cool.request.components.api.export.ApiExport;
import com.cool.request.components.api.export.ExportCondition;
import com.cool.request.utils.MessagesWrapperUtils;
import com.cool.request.utils.ProgressWindowWrapper;
import com.cool.request.utils.ResourceBundleUtils;
import com.cool.request.utils.StringUtils;
import com.cool.request.view.dialog.SettingDialog;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class ApiFoxExport implements ApiExport {
    private final ApifoxAPI apifoxAPI = new ApifoxAPI();
    private final Project project;

    public ApiFoxExport(Project project) {
        this.project = project;
    }

    @Override
    public boolean canExport() {

        return checkToken(new ApiFoxExportCondition(SettingPersistentState.getInstance().getState().apiFoxAuthorization,
                SettingPersistentState.getInstance().getState().openApiToken))
                .values()
                .stream().allMatch(aBoolean -> aBoolean);
    }

    @Override
    public void showCondition() {
        Configurable[] newConfigurable = SettingDialog.createNewConfigurable(project);
        SettingDialog.show(project, newConfigurable, 3);
    }

    @Override
    public Map<String, Boolean> checkToken(ExportCondition exportCondition) {
        Map<String, Boolean> resultMap = new HashMap<>();
        if (exportCondition instanceof ApiFoxExportCondition) {
            ApiFoxExportCondition apiFoxExportCondition = (ApiFoxExportCondition) exportCondition;
            String userInfo = apifoxAPI.getUserInfo(apiFoxExportCondition.getApiFoxAuthorization());
            boolean result = !StringUtils.isEmpty(userInfo);
            if (result) {
                SettingPersistentState.getInstance().getState().apiFoxAuthorization = (apiFoxExportCondition.getApiFoxAuthorization());
            }
            Map<String, Object> openTokenResult = apifoxAPI.exportApiAndGet(0, new HashMap<>());
            resultMap.put(ApiFoxExportCondition.KEY_API_FOX_AUTHORIZATION, result);
            resultMap.put(ApiFoxExportCondition.KEY_API_FOX_OPEN_AUTHORIZATION, openTokenResult.getOrDefault("errorMessage", "").equals("Not found"));
        }
        return resultMap;
    }

    private void doExport(String json, ApifoxFolder.Folder folder) {
        if (folder == null) {
            MessagesWrapperUtils.showErrorDialog("Export fail", "Tip");
            return;
        }
        ProgressWindowWrapper.newProgressWindowWrapper(project).run(new Task.Backgroundable(project, "Export") {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                Map<String, Object> data = new HashMap<>();
                data.put("importFormat", "openapi");
                data.put("apiOverwriteMode", "methodAndPath");
                data.put("schemaOverwriteMode", "merge");
                data.put("data", json);
                data.put("apiFolderId", folder.getId());
                Map<String, Object> result = apifoxAPI.exportApiAndGet(folder.getProjectId(), data);
                if (result.getOrDefault("success", false).equals(Boolean.TRUE)) {
                    SwingUtilities.invokeLater(() -> Messages.showMessageDialog("Export success", ResourceBundleUtils.getString("tip"), Messages.getWarningIcon()));
                } else {
                    SwingUtilities.invokeLater(() -> Messages.showErrorDialog("Export fail:" + result.getOrDefault("errorMessage", ""), ResourceBundleUtils.getString("tip")));
                }
            }
        });

    }

    @Override
    public boolean export(String json) {
        ApifoxProjectFolderSelectDialog.showDialog(project, apifoxAPI, (s) -> doExport(json, s));
        return false;
    }
}
