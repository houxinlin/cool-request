/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * StringBody.java is part of Cool Request
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

import com.cool.request.components.http.net.MediaTypes;

import java.nio.charset.StandardCharsets;

public class StringBody implements Body {
    @Override
    public byte[] contentConversion() {
        if (value == null) return new byte[]{};
        return value.getBytes(StandardCharsets.UTF_8);
    }
    @Override
    public String getMediaType() {
        return MediaTypes.TEXT;
    }
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public StringBody(String value) {
        this.value = value;
    }
}
