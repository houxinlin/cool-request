/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * HttpMethodIconUtils.java is part of Cool Request
 *
 * License: GPL-3.0+
 *
 * Cool Request is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Cool Request is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Cool Request.  If not, see <http://www.gnu.org/licenses/>.
 */

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
                return CoolRequestIcons.DELETE_METHOD;
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
