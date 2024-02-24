package com.cool.request.utils;

import com.cool.request.common.bean.components.controller.Controller;
import com.cool.request.common.bean.components.controller.CustomController;

public class ControllerUtils {
    private static String orDefault(String value) {
        if (value == null) return "";
        return value;
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
