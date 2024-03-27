/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * HTTPParameterProvider.java is part of Cool Request
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

package com.cool.request.utils.param;

import com.cool.request.common.bean.RequestEnvironment;
import com.cool.request.components.http.Controller;
import com.cool.request.components.http.net.HttpMethod;
import com.cool.request.components.http.KeyValue;
import com.cool.request.lib.springmvc.Body;
import com.intellij.openapi.project.Project;

import java.util.ArrayList;
import java.util.List;

/**
 * 参数提供器，用来和全局变量合并参数
 */
public interface HTTPParameterProvider {
    /**
     * 获取url路径,不带content-path
     */
    public String getFullUrl(Project project, Controller controller, RequestEnvironment environment);

    /**
     * 获取HTTP方法
     */

    public HttpMethod getHttpMethod(Project project, Controller controller, RequestEnvironment environment);

    /**
     * 获取请求头
     */
    public List<KeyValue> getHeader(Project project, Controller controller, RequestEnvironment environment);

    /**
     * 获取url参数
     */
    public List<KeyValue> getUrlParam(Project project, Controller controller, RequestEnvironment environment);


    /**
     * 获取path变量
     */
    public default List<KeyValue> getPathParam(Project project) {
        return new ArrayList<>();
    }

    /**
     * 获取请求体
     */
    public Body getBody(Project project, Controller controller, RequestEnvironment environment);

}
