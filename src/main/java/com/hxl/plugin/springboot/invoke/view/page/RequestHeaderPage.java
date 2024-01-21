package com.hxl.plugin.springboot.invoke.view.page;

import com.hxl.plugin.springboot.invoke.net.MapRequest;
import com.hxl.plugin.springboot.invoke.net.request.ControllerRequestData;
import com.hxl.plugin.springboot.invoke.view.BasicKeyValueTableParamJPanel;
import com.intellij.openapi.project.Project;

import java.util.HashMap;
import java.util.Map;

public class RequestHeaderPage  extends BasicKeyValueTableParamJPanel implements MapRequest {
    public RequestHeaderPage(Project project) {
        super(project);
    }

    @Override
    public void configRequest(ControllerRequestData controllerRequestData) {
        Map<String,Object>  header =new HashMap<>();
        foreach(header::put);
        header.forEach((s, o) -> controllerRequestData.addHeader(s, o.toString()));
    }


}
