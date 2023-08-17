package com.hxl.plugin.springboot.invoke.view.page;

import com.hxl.plugin.springboot.invoke.invoke.ControllerInvoke;
import com.hxl.plugin.springboot.invoke.net.MapRequest;
import com.hxl.plugin.springboot.invoke.view.BasicTableParamJPanel;

import java.util.HashMap;
import java.util.Map;

public class RequestHeaderPage  extends BasicTableParamJPanel implements MapRequest {
    @Override
    public void configRequest(ControllerInvoke.ControllerRequestData controllerRequestData) {
        Map<String,Object>  header =new HashMap<>();
        foreach(header::put);
        controllerRequestData.setHeaders(header);
    }
}
