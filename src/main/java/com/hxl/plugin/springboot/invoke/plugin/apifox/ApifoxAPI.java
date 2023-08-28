package com.hxl.plugin.springboot.invoke.plugin.apifox;

import com.hxl.plugin.springboot.invoke.net.OkHttpRequest;
import com.hxl.plugin.springboot.invoke.utils.ObjectMappingUtils;
import okhttp3.Call;
import okhttp3.OkHttpClient;

import java.text.MessageFormat;
import java.util.Map;

public class ApifoxAPI extends OkHttpRequest {
    private static final String HOST="https://api.apifox.cn";
    private static final String IMPORT_URL="/api/v1/projects/{0}/import-data";
    @Override
    public OkHttpClient init(OkHttpClient.Builder builder) {
        addHeaderInterceptor(builder,"Authorization","Bearer APS-P8jWdLllKxdbn8IB3Abz7iGmLW6Mr1U2");
        addHeaderInterceptor(builder,"X-Apifox-Version","2022-11-16");
        return builder.build();
    }

    public Call exportApi(Integer projectId,Map<String,Object> body){
        String url = MessageFormat.format(IMPORT_URL, projectId.toString());
        return postBody(HOST.concat(url), ObjectMappingUtils.toJsonString(body),"application/json");
    }
}
