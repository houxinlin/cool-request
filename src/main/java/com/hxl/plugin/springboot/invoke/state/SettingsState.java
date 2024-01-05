package com.hxl.plugin.springboot.invoke.state;

public class SettingsState {
    public String apiFoxAuthorization;
    public String openApiToken;
    public int languageValue;
    public String environmentSelectId;

    public SettingsState() {
        this.apiFoxAuthorization = "";
        this.openApiToken = "";
        this.languageValue = 0;
        this.environmentSelectId = "";
    }
}
