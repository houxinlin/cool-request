package com.hxl.plugin.springboot.invoke.state;

import com.intellij.openapi.components.BaseState;

import java.util.Objects;

public class SettingsState  extends BaseState {
    public String apifoxAuthorization="";
    public String openApiToken;

    public String getApifoxAuthorization() {
        return apifoxAuthorization;
    }

    public void setApifoxAuthorization(String apifoxAuthorization) {
        this.apifoxAuthorization = apifoxAuthorization;
    }

    public String getOpenApiToken() {
        return openApiToken;
    }

    public void setOpenApiToken(String openApiToken) {
        this.openApiToken = openApiToken;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SettingsState that = (SettingsState) o;
        return Objects.equals(apifoxAuthorization, that.apifoxAuthorization) && Objects.equals(openApiToken, that.openApiToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), apifoxAuthorization, openApiToken);
    }
}
