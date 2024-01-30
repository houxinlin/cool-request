package com.cool.request.view.dialog;

public class BaseSetting {
    private  int languageIndex;
    private boolean enableDynamicRefresh;
    private boolean autoRefreshData;
    private boolean listenerGateway;
    private boolean autoNavigation;
    private boolean mergeApiAndRequest;
    private String proxyIp;
    private int proxyPort;
    private boolean parameterCoverage;

    public String getProxyIp() {
        return proxyIp;
    }

    public void setProxyIp(String proxyIp) {
        this.proxyIp = proxyIp;
    }

    public int getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
    }

    public boolean isParameterCoverage() {
        return parameterCoverage;
    }

    public void setParameterCoverage(boolean parameterCoverage) {
        this.parameterCoverage = parameterCoverage;
    }

    public boolean isMergeApiAndRequest() {
        return mergeApiAndRequest;
    }

    public void setMergeApiAndRequest(boolean mergeApiAndRequest) {
        this.mergeApiAndRequest = mergeApiAndRequest;
    }

    public boolean isEnableDynamicRefresh() {
        return enableDynamicRefresh;
    }

    public void setEnableDynamicRefresh(boolean enableDynamicRefresh) {
        this.enableDynamicRefresh = enableDynamicRefresh;
    }

    public boolean isAutoRefreshData() {
        return autoRefreshData;
    }

    public void setAutoRefreshData(boolean autoRefreshData) {
        this.autoRefreshData = autoRefreshData;
    }

    public boolean isListenerGateway() {
        return listenerGateway;
    }

    public void setListenerGateway(boolean listenerGateway) {
        this.listenerGateway = listenerGateway;
    }

    public boolean isAutoNavigation() {
        return autoNavigation;
    }

    public void setAutoNavigation(boolean autoNavigation) {
        this.autoNavigation = autoNavigation;
    }

    public BaseSetting(int languageIndex) {
        this.languageIndex = languageIndex;
    }


    public BaseSetting() {
    }

    public int getLanguageIndex() {
        return languageIndex;
    }

    public void setLanguageIndex(int languageIndex) {
        this.languageIndex = languageIndex;
    }
}
