/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * DataTypeUtils.java is part of Cool Request
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

public class DataTypeUtils {
    public static String getDataType(Object object) {
        if (object == null) return "string";
        if ("true".equalsIgnoreCase(object.toString()) || "false".equalsIgnoreCase(object.toString())) return "boolean";
        if (object instanceof String) return "string";
        if (object instanceof Integer || object instanceof Long) return "integer";
        if (object instanceof Float || object instanceof Double) return "integer";
        if (object instanceof Short) return "integer";
        if (object instanceof Boolean) return "boolean";
        return "string";
    }

}
