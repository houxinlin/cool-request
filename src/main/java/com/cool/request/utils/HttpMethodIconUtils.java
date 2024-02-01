package com.cool.request.utils;

import com.cool.request.common.icons.CoolRequestIcons;

import javax.swing.*;

/**
 * @author zhangpengjun
 * @date 2024/1/12
 */
public class HttpMethodIconUtils {

    public static Icon getIconByHttpMethod(String method) {
        switch (method.toUpperCase()) {
            case "GET":
                return CoolRequestIcons.GET_METHOD;
            case "POST":
                return CoolRequestIcons.POST_METHOD;
            case "DELETE":
                return CoolRequestIcons.DELTE_METHOD;
            case "PUT":
                return CoolRequestIcons.PUT_METHOD;
            case "HEAD":
                return CoolRequestIcons.HEAD_METHOD;
            case "OPTIONS":
                return CoolRequestIcons.OPTIONS_METHOD;
            case "PATCH":
                return CoolRequestIcons.PATCH_METHOD;
            case "TRACE":
                return CoolRequestIcons.TRACE_METHOD;
        }
        return CoolRequestIcons.POST_METHOD;
    }

}
