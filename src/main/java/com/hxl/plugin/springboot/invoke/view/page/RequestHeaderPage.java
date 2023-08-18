package com.hxl.plugin.springboot.invoke.view.page;

import com.hxl.plugin.springboot.invoke.invoke.ControllerInvoke;
import com.hxl.plugin.springboot.invoke.net.MapRequest;
import com.hxl.plugin.springboot.invoke.view.BasicTableParamJPanel;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class RequestHeaderPage  extends BasicTableParamJPanel implements MapRequest {
    @Override
    public void configRequest(ControllerInvoke.ControllerRequestData controllerRequestData) {
        Map<String,Object>  header =new HashMap<>();
        foreach(header::put);
        header.forEach((s, o) -> controllerRequestData.addHeader(s, o.toString()));
    }
}
