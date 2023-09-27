package com.hxl.plugin.springboot.invoke.state;

import java.util.HashMap;
import java.util.Map;

public class RequestCacheState {
    public Map<String,String> headerMap =new HashMap<>();
    public Map<String,byte[]> responseBodyMap =new HashMap<>();
    public Map<String,Integer> responseCode =new HashMap<>();
    public RequestCacheState() {
    }
}
