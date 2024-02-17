package com.cool.request.common.state;

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
    public String proxyIp;
    public int proxyPort;
    public boolean parameterCoverage;

    public boolean enabledScriptLibrary;
    public boolean addQuickSendButtonOnMethodLeft;

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
        parameterCoverage = false;
        proxyPort = 0;
        proxyIp = "";
        enabledScriptLibrary = false;
        addQuickSendButtonOnMethodLeft=true;
    }
}
