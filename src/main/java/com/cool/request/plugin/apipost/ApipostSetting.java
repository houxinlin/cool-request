package com.cool.request.plugin.apipost;

public class ApipostSetting {
    private String apipostHost;
    private String apipostApiToken;

    public ApipostSetting(String apipostHost, String apipostApiToken) {
        this.apipostHost = apipostHost;
        this.apipostApiToken = apipostApiToken;
    }

    public ApipostSetting() {
    }

    public String getApipostHost() {
        return apipostHost;
    }

    public void setApipostHost(String apipostHost) {
        this.apipostHost = apipostHost;
    }

    public String getApipostApiToken() {
        return apipostApiToken;
    }

    public void setApipostApiToken(String apipostApiToken) {
        this.apipostApiToken = apipostApiToken;
    }
}
