package com.cool.request.view.page;

import com.cool.request.component.http.net.KeyValue;
import com.cool.request.component.http.net.RequestParamApply;
import com.cool.request.component.http.net.request.StandardHttpRequestParam;
import com.cool.request.utils.ClassResourceUtils;
import com.cool.request.utils.StringUtils;
import com.cool.request.view.BasicKeyValueTablePanelParamPanel;
import com.intellij.openapi.project.Project;

import java.awt.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestHeaderPage extends BasicKeyValueTablePanelParamPanel implements RequestParamApply {
    public RequestHeaderPage(Project project, Window window) {
        super(project, window);
    }

    public RequestHeaderPage(Project project) {
        super(project);
    }

    @Override
    public void configRequest(StandardHttpRequestParam standardHttpRequestParam) {
        Map<String, Object> header = new HashMap<>();
        foreach(header::put);
        header.forEach((s, o) -> standardHttpRequestParam.getHeaders().add(new KeyValue(s, o.toString())));
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
