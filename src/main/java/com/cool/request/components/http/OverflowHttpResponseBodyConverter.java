/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * OverflowHttpResponseBodyConverter.java is part of Cool Request
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

package com.cool.request.components.http;

import com.cool.request.common.state.SettingPersistentState;
import com.cool.request.components.http.net.HTTPHeader;
import com.cool.request.components.http.net.MediaTypes;
import com.cool.request.utils.ResourceBundleUtils;

public class OverflowHttpResponseBodyConverter implements HTTPResponseBodyConverter {
    @Override
    public byte[] bodyConverter(byte[] httpResponseBody, HTTPHeader header) {
        int maxSize = SettingPersistentState.getInstance().getState().maxHTTPResponseSize * 1024 * 1024;
        if (httpResponseBody.length >maxSize) {
            header.setHeader(HTTPHeader.CONTENT_TYPE, MediaTypes.TEXT);
            return ResourceBundleUtils.getString("big.data.reject").getBytes();
        } else {
            return httpResponseBody;
        }
    }
}
