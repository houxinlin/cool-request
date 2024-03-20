package com.cool.request.components.http;

import com.cool.request.common.bean.components.DynamicComponent;
import com.cool.request.components.CanMark;

import java.util.ArrayList;

public class DynamicController extends Controller implements DynamicComponent, CanMark {
    private static final long serialVersionUID = 1000000000;

    public DynamicController() {
    }

    public DynamicController(Controller controller) {
        copyProperties(controller, this);
    }

    private void copyProperties(Controller source, DynamicController target) {
        target.setModuleName(source.getModuleName());
        target.setContextPath(source.getContextPath());
        target.setServerPort(source.getServerPort());
        target.setUrl(source.getUrl());
        target.setSimpleClassName(source.getSimpleClassName());
        target.setMethodName(source.getMethodName());
        target.setHttpMethod(source.getHttpMethod());
        target.setParamClassList(new ArrayList<>(source.getParamClassList()));
    }
}
