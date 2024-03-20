package com.cool.request.plugin.apifox;

import com.cool.request.components.api.export.ExportCondition;

public class ApiFoxExportCondition  implements ExportCondition {
    public static final String KEY_API_FOX_AUTHORIZATION="apiFoxAuthorization";
    public static final String KEY_API_FOX_OPEN_AUTHORIZATION="openApiToken";
    private String apiFoxAuthorization="";
    private String openApiToken="";

    public ApiFoxExportCondition(String apiFoxAuthorization, String openApiToken) {
        this.apiFoxAuthorization = apiFoxAuthorization;
        this.openApiToken = openApiToken;
    }

    public String getApiFoxAuthorization() {
        return apiFoxAuthorization;
    }

    public void setApiFoxAuthorization(String apiFoxAuthorization) {
        this.apiFoxAuthorization = apiFoxAuthorization;
    }

    public String getOpenApiToken() {
        return openApiToken;
    }

    public void setOpenApiToken(String openApiToken) {
        this.openApiToken = openApiToken;
    }
}
