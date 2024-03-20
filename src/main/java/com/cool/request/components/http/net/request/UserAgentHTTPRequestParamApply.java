package com.cool.request.components.http.net.request;

import com.cool.request.common.state.SettingPersistentState;
import com.cool.request.common.state.SettingsState;
import com.cool.request.components.http.net.HTTPRequestParamApply;
import com.cool.request.components.http.KeyValue;
import com.intellij.openapi.project.Project;

public class UserAgentHTTPRequestParamApply implements HTTPRequestParamApply {
    @Override
    public void apply(Project project, StandardHttpRequestParam httpRequestParam) {
        SettingsState state = SettingPersistentState.getInstance().getState();
        if (state.requestAddUserAgent){
           httpRequestParam.getHeaders().add(new KeyValue("User-Agent",state.userAgent));
       }
    }
}
