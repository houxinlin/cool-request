package com.hxl.plugin.springboot.invoke.view.page;

import com.hxl.plugin.springboot.invoke.net.MapRequest;
import com.hxl.plugin.springboot.invoke.net.request.ControllerRequestData;
import com.hxl.plugin.springboot.invoke.utils.UrlUtils;
import com.hxl.plugin.springboot.invoke.view.BasicKeyValueTablePanelParamPanelImpl;
import com.intellij.openapi.project.Project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class UrlPanelParamPageImpl extends BasicKeyValueTablePanelParamPanelImpl implements MapRequest {
    public UrlPanelParamPageImpl(Project project) {
        super(project);
    }

    @Override
    public void configRequest(ControllerRequestData controllerRequestData) {
        Map<String, List<String>> param = new HashMap<>();
        foreach((key, value) -> param.computeIfAbsent(key, s -> new ArrayList<>()).add(value));
        String url = controllerRequestData.getUrl();
        //asd?
        //asd?name=1
        //asd?name=a&
        //asd?&
        //asd?
        if (url.indexOf('?') == -1 && !url.endsWith("?")) url = url.concat("?");
        if (!url.endsWith("&") && !param.isEmpty() && !url.endsWith("?")) url = url.concat("&");
        controllerRequestData.setUrl(url.concat(UrlUtils.mapToUrlParams(param)));
    }
}
