package com.hxl.plugin.springboot.invoke.net.request;

import com.hxl.plugin.springboot.invoke.net.HttpMethod;
import com.hxl.plugin.springboot.invoke.net.KeyValue;
import com.hxl.plugin.springboot.invoke.springmvc.Body;

import java.util.ArrayList;
import java.util.List;

public class StandardHttpRequestParam extends HttpRequestParam {
    private String url;  //url路径
    private final List<KeyValue> headers = new ArrayList<>(); //请求头
    private final List<KeyValue> urlParam = new ArrayList<>(); //url参数
    private Body body; //post body
    private HttpMethod method; //http方法

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    public List<KeyValue> getHeaders() {
        return headers;
    }

    public List<KeyValue> getUrlParam() {
        return urlParam;
    }

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }
}
