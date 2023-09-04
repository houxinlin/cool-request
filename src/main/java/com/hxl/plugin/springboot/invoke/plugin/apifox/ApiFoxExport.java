package com.hxl.plugin.springboot.invoke.plugin.apifox;

import com.hxl.plugin.springboot.invoke.export.ApiExport;
import com.hxl.plugin.springboot.invoke.net.OkHttpRequest;
import com.hxl.plugin.springboot.invoke.state.SettingPersistentState;
import com.hxl.plugin.springboot.invoke.utils.StringUtils;
import com.hxl.plugin.springboot.invoke.view.dialog.SettingDialog;
import invoke.dsl.ApifoxConfiburable;
import com.intellij.ide.actions.ShowSettingsUtilImpl;
import com.intellij.openapi.options.*;
import com.intellij.openapi.options.ex.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;

/**
 * api??
 */
public class ApiFoxExport implements ApiExport {
    private final ApifoxAPI apifoxAPI = new ApifoxAPI();

    @Override
    public boolean canExport() {
        return checkCookie(SettingPersistentState.getInstance().getState().apifoxAuthorization);
    }

    @Override
    public void showCondition() {
        SettingDialog.show();
    }

    @Override
    public boolean checkCookie(String cookie) {
        String userInfo = apifoxAPI.getUserInfo(cookie);
        boolean result = !StringUtils.isEmpty(userInfo);
        if (result) SettingPersistentState.getInstance().getState().apifoxAuthorization = cookie;
        return result;
    }

    @Override
    public boolean export(String json) {
        Map<String, Object> data = new HashMap<>();
        data.put("importFormat", "openapi");
        data.put("apiOverwriteMode", "methodAndPath");
        data.put("schemaOverwriteMode", "merge");
        data.put("data", json);
        apifoxAPI.exportApi(2877496, data).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                System.out.println(response.body().string());
            }
        });
        return false;
    }
}
