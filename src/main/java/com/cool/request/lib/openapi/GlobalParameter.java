package com.cool.request.lib.openapi;

import com.cool.request.common.bean.EmptyEnvironment;
import com.cool.request.common.bean.RequestEnvironment;
import com.cool.request.common.constant.CoolRequestConfigConstant;
import com.cool.request.components.http.FormDataInfo;
import com.cool.request.components.http.KeyValue;
import com.cool.request.view.tool.provider.RequestEnvironmentProvideImpl;
import com.intellij.openapi.project.Project;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GlobalParameter {
    public static List<KeyValue> getGlobalHeader(Project project) {
        RequestEnvironment selectRequestEnvironment = RequestEnvironmentProvideImpl.getInstance(project).getSelectRequestEnvironment();
        if (selectRequestEnvironment instanceof EmptyEnvironment) return new ArrayList<>();

        return selectRequestEnvironment.getHeader();
    }

    public static List<FormDataInfo> getGlobalFormData(Project project) {
        RequestEnvironment selectRequestEnvironment = RequestEnvironmentProvideImpl.getInstance(project).getSelectRequestEnvironment();
        if (selectRequestEnvironment instanceof EmptyEnvironment) return new ArrayList<>();

        return selectRequestEnvironment.getFormData();
    }
}
