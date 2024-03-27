/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * ApiExport.java is part of Cool Request
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

package com.cool.request.components.api.export;

import com.cool.request.components.http.Controller;

import java.util.List;
import java.util.Map;

public interface ApiExport {

    /**
     * 导出
     *
     * @param json 导出的json
     * @return 是否导出成功
     */
    boolean export(String json);

    default boolean export(List<Controller> controllers) {
        return false;
    }

    /**
     * 判断是否可以导出
     *
     * @return 是否可以导出
     */
    boolean canExport();

    /**
     * 不能导出时展示条件
     */
    void showCondition();

    /**
     * 检测Token
     *
     * @param exportCondition 导出条件
     * @return 检测结果
     */
    Map<String, Boolean> checkToken(ExportCondition exportCondition);
}
