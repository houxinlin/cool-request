package com.cool.request.lib.springmvc;

import com.cool.request.components.http.Controller;

import java.util.ArrayList;

public class DefaultMethodDescription extends MethodDescription {
    public DefaultMethodDescription(Controller controller) {
        this.setDescription(controller.getUrl());
        this.setMethodName("");
        this.setDescription(controller.getUrl());
        this.setClassName("");
        this.setSummary(controller.getUrl());
        this.setParameters(new ArrayList<>());
    }
}
