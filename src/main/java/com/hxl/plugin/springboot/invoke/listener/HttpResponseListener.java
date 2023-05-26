package com.hxl.plugin.springboot.invoke.listener;

import java.util.List;
import java.util.Map;

public interface HttpResponseListener extends CommunicationListener {
    public void onResponse(String requestId, Map<String, List<String>> headers, byte[] response);
}
