package com.cool.request.utils;

import com.cool.request.common.bean.components.controller.Controller;
import com.cool.request.common.bean.components.scheduled.SpringScheduled;
import com.intellij.openapi.project.Project;

public class ComponentIdUtils {
    public static String getMd5(Project project, Controller controller) {
        String id = new StringBuilder()
                .append(project.getName())
                .append(controller.getModuleName())
                .append(controller.getSimpleClassName())
                .append(controller.getMethodName())
                .append(controller.getHttpMethod())
                .append(controller.getUrl())
                .toString();
        return StringUtils.calculateMD5(id);
    }

    public static String getMd5(Project project, SpringScheduled controller) {
        String id = new StringBuilder()
                .append(project.getName())
                .append(controller.getModuleName())
                .append(controller.getClassName())
                .append(controller.getMethodName())
                .toString();
        return StringUtils.calculateMD5(id);
    }

}
