package com.hxl.plugin.springboot.invoke.view.page;

import com.hxl.plugin.springboot.invoke.net.MapRequest;
import com.hxl.plugin.springboot.invoke.net.request.ControllerRequestData;
import com.hxl.plugin.springboot.invoke.utils.ClassResourceUtils;
import com.hxl.plugin.springboot.invoke.utils.StringUtils;
import com.hxl.plugin.springboot.invoke.view.BasicKeyValueTablePanelParamPanelImpl;
import com.intellij.openapi.project.Project;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestHeaderPage extends BasicKeyValueTablePanelParamPanelImpl implements MapRequest {
    public RequestHeaderPage(Project project) {
        super(project);
    }

    @Override
    public void configRequest(ControllerRequestData controllerRequestData) {
        Map<String, Object> header = new HashMap<>();
        foreach(header::put);
        header.forEach((s, o) -> controllerRequestData.addHeader(s, o.toString()));
    }

    @Override
    protected List<String> getKeySuggest() {
        return ClassResourceUtils.readLines("/txt/header.txt");
    }

    @Override
    protected List<String> getValueSuggest(String key) {
        String fileName = ("/txt/" + key + ".values").toLowerCase();
        if (StringUtils.isEmpty(fileName)) return Collections.EMPTY_LIST;
        if (!ClassResourceUtils.exist(fileName)) return Collections.EMPTY_LIST;
        return ClassResourceUtils.readLines(fileName);
    }
}
