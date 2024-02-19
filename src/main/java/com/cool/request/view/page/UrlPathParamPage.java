package com.cool.request.view.page;

import com.cool.request.component.http.net.RequestParamApply;
import com.cool.request.component.http.net.request.StandardHttpRequestParam;
import com.cool.request.view.BasicKeyValueTablePanelParamPanel;
import com.intellij.openapi.project.Project;

public class UrlPathParamPage  extends BasicKeyValueTablePanelParamPanel implements RequestParamApply {
    public UrlPathParamPage(Project project) {
        super(project);
    }

    @Override
    public void configRequest(StandardHttpRequestParam standardHttpRequestParam) {

    }
}
