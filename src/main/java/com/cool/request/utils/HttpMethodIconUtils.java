package com.cool.request.utils;

import com.cool.request.icons.MyIcons;

import javax.swing.*;

/**
 * @author zhangpengjun
 * @date 2024/1/12
 */
public class HttpMethodIconUtils {

    public static Icon getIconByHttpMethod(String method) {
        switch (method.toUpperCase()) {
            case "GET":
                return MyIcons.GET_METHOD;
            case "POST":
                return MyIcons.POST_METHOD;
            case "DELETE":
                return MyIcons.DELTE_METHOD;
            case "PUT":
                return MyIcons.PUT_METHOD;
            case "HEAD":
                return MyIcons.HEAD_METHOD;
            case "OPTIONS":
                return MyIcons.OPTIONS_METHOD;
            case "PATCH":
                return MyIcons.PATCH_METHOD;
            case "TRACE":
                return MyIcons.TRACE_METHOD;
        }
        return MyIcons.POST_METHOD;
    }

}
