package com.hxl.plugin.springboot.invoke.plugin.apifox;

import com.google.protobuf.Message;
import com.hxl.plugin.springboot.invoke.export.ApiExport;
import com.hxl.plugin.springboot.invoke.export.ExportCondition;
import com.hxl.plugin.springboot.invoke.state.SettingPersistentState;
import com.hxl.plugin.springboot.invoke.utils.StringUtils;
import com.hxl.plugin.springboot.invoke.view.dialog.SettingDialog;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.messages.MessageDialog;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ApiFoxExport implements ApiExport {
    private final ApifoxAPI apifoxAPI = new ApifoxAPI();
    private static Map<String, byte[]> iconMap = new HashMap<>();

    //    private final JTree jTree
    @Override
    public boolean canExport() {
        return checkToken(new ApiFoxExportCondition(SettingPersistentState.getInstance().getState().apiFoxAuthorization,
                SettingPersistentState.getInstance().getState().openApiToken))
                .values()
                .stream().allMatch(aBoolean -> aBoolean);
    }

    @Override
    public void showCondition() {
        SettingDialog.show();
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

    public static byte[] getIconData(String url) {
        return iconMap.get(url);
    }

    private void doExport(String json, ApifoxFolder.Folder folder) {
        Map<String, Object> data = new HashMap<>();
        data.put("importFormat", "openapi");
        data.put("apiOverwriteMode", "methodAndPath");
        data.put("schemaOverwriteMode", "merge");
        data.put("data", json);
        data.put("apiFolderId", folder.getId());
        Map<String, Object> result = apifoxAPI.exportApiAndGet(folder.getProjectId(), data);
        if (result.getOrDefault("success", false).equals(Boolean.TRUE)) {
            Messages.showMessageDialog("导出成功", "提示", Messages.getWarningIcon());
        } else {
            Messages.showErrorDialog("导出失败:" + result.getOrDefault("errorMessage", ""), "提示");
        }

    }

    @Override
    public boolean export(String json) {
        ProjectDialog.showDialog(apifoxAPI, (s) -> doExport(json, s));
        return false;
    }
}
