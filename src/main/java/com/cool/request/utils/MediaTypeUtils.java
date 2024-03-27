/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * MediaTypeUtils.java is part of Cool Request
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

public class MediaTypeUtils {
    private static boolean contains(String src, String target) {
        if (StringUtils.isEmpty(src)) return false;
        if (target.isEmpty()) return false;
        return src.toLowerCase().contains(target.toLowerCase());
    }

    public static boolean isApplication(String src, String target) {
        return contains(src, "application/" + target);
    }

    public static boolean isFormData(String src) {
        return contains(src, "multipart/form-data");
    }

    public static boolean isXml(String src) {
        return isApplication(src, "xml") || contains(src, "text/xml");
    }

    public static boolean isJson(String src) {
        return isApplication(src, "json");
    }

    public static boolean isFormUrlencoded(String src) {
        return isApplication(src, "x-www-form-urlencoded");
    }

}
