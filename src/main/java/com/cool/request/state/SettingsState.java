package com.cool.request.state;

public class SettingsState {
    public String apiFoxAuthorization;
    public String openApiToken;
    public int languageValue;
    public String environmentSelectId;
    public boolean enableDynamicRefresh;
    public boolean autoRefreshData;
    public boolean listenerGateway;
    public boolean autoNavigation;

    public boolean mergeApiAndRequest;

    public SettingsState() {
        this.apiFoxAuthorization = "";
        this.openApiToken = "";
        this.languageValue = 0;
        this.environmentSelectId = "";
        autoNavigation = true;
        listenerGateway = true;
        autoRefreshData = true;
        enableDynamicRefresh = true;
        mergeApiAndRequest = false;
    }
}
