/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * FormUrlBody.java is part of Cool Request
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

package com.cool.request.lib.springmvc;

import com.cool.request.components.http.KeyValue;
import com.cool.request.components.http.net.MediaTypes;
import com.cool.request.utils.UrlUtils;

import java.util.ArrayList;
import java.util.List;

public class FormUrlBody implements Body {
    private List<KeyValue> data;

    public FormUrlBody(List<KeyValue> tableMap) {
        this.data = tableMap;
        if (data == null) data = new ArrayList<>();
    }

    public List<KeyValue> getData() {
        return data;
    }
    @Override
    public String getMediaType() {
        return MediaTypes.APPLICATION_WWW_FORM;
    }
    @Override
    public byte[] contentConversion() {
        return UrlUtils.mapToUrlParams(data).getBytes();
    }
}
