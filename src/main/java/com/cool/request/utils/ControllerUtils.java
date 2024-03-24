package com.cool.request.utils;

import com.cool.request.components.http.Controller;
import com.cool.request.components.http.CustomController;

public class ControllerUtils {
    public static void copy(Controller src, Controller dest) {
        if (src == null || dest == null) return;
        dest.setMethodName(src.getModuleName());
        dest.setContextPath(src.getContextPath());
        dest.setServerPort(src.getServerPort());
        dest.setUrl(src.getUrl());
        dest.setSimpleClassName(src.getSimpleClassName());
        dest.setMethodName(src.getMethodName());
        dest.setHttpMethod(src.getHttpMethod());
        dest.setParamClassList(src.getParamClassList());
        dest.setSuperPsiClass(src.getSuperPsiClass());
        dest.setOwnerPsiMethod(src.getOwnerPsiMethod());

    }

    private static String orDefault(String value) {
        if (value == null) return "";
        return value;
    }

    public static String buildLocalhostUrl(Controller controller) {
        String host = "http://localhost:" + controller.getServerPort();
        return StringUtils.joinUrlPath(host, controller.getContextPath(), controller.getUrl());
    }

    /**
     * 获取完整的url路径
     */
    public static String getFullUrl(Controller controller) {
        //自定义controller直接返回
        if (controller instanceof CustomController) return controller.getUrl();
        String url = controller.getUrl();
        if (StringUtils.isEmpty(url)) return orDefault(controller.getContextPath());
        if (!url.startsWith("/")) url = "/" + url;
        return StringUtils.joinUrlPath(orDefault(controller.getContextPath()), url);
    }

}
