package com.cool.request.plugin.apifox;

public class ApifoxSetting {
    private String httpToken;
    private String apiToken;

    public ApifoxSetting(String httpToken, String apiToken) {
        this.httpToken = httpToken;
        this.apiToken = apiToken;
    }

    public ApifoxSetting() {
    }

    public String getHttpToken() {
        return httpToken;
    }

    public void setHttpToken(String httpToken) {
        this.httpToken = httpToken;
    }

    public String getApiToken() {
        return apiToken;
    }

    public void setApiToken(String apiToken) {
        this.apiToken = apiToken;
    }
}
