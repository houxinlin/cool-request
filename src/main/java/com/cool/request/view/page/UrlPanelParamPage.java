package com.cool.request.view.page;

import com.cool.request.component.http.net.RequestParamApply;
import com.cool.request.component.http.net.request.StandardHttpRequestParam;
import com.cool.request.utils.UrlUtils;
import com.cool.request.view.BasicKeyValueTablePanelParamPanel;
import com.intellij.openapi.project.Project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class UrlPanelParamPage extends BasicKeyValueTablePanelParamPanel implements RequestParamApply {
    public UrlPanelParamPage(Project project) {
        super(project);
    }

    @Override
    public void configRequest(StandardHttpRequestParam standardHttpRequestParam) {
        Map<String, List<String>> param = new HashMap<>();
        foreach((key, value) -> param.computeIfAbsent(key, s -> new ArrayList<>()).add(value));
        String url = standardHttpRequestParam.getUrl();
        //asd?
        //asd?name=1
        //asd?name=a&
        //asd?&
        //asd?
        if (url.indexOf('?') == -1 && !url.endsWith("?")) url = url.concat("?");
        if (!url.endsWith("&") && !param.isEmpty() && !url.endsWith("?")) url = url.concat("&");
        standardHttpRequestParam.setUrl(url.concat(UrlUtils.mapToUrlParams(param)));
    }
}
