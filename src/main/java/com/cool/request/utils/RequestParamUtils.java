/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * RequestParamUtils.java is part of Cool Request
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

import com.cool.request.components.http.KeyValue;

import java.util.ArrayList;
import java.util.List;

public class RequestParamUtils {
    public static String getFirstValue(List<KeyValue> list, String key) {
        for (KeyValue keyValue : list) {
            if (StringUtils.isEquals(keyValue.getKey(), key)) return keyValue.getValue();
        }
        return null;
    }

    public static List<String> getValues(List<KeyValue> list, String key) {
        List<String> result = new ArrayList<>();
        for (KeyValue keyValue : list) {
            if (StringUtils.isEquals(keyValue.getKey(), key)) result.add(keyValue.getValue());
        }
        return result;
    }
}
