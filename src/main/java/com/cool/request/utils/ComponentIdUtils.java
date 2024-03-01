package com.cool.request.utils;

import com.cool.request.common.bean.components.controller.Controller;
import com.cool.request.common.bean.components.scheduled.BasicScheduled;
import com.intellij.openapi.project.Project;

public class ComponentIdUtils {

    public static String getMd5(Project project, Controller controller) {
        String id = project.getName() +
                controller.getModuleName() +
                controller.getSimpleClassName() +
                controller.getMethodName() +
                controller.getHttpMethod() +
                controller.getUrl();
        return StringUtils.calculateMD5(id);
    }

    public static String getMd5(Project project, BasicScheduled controller) {
        String id = project.getName() +
                controller.getModuleName() +
                controller.getClassName() +
                controller.getMethodName();
        return StringUtils.calculateMD5(id);
    }

}
