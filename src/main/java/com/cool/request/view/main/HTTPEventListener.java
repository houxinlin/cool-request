/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * HTTPEventListener.java is part of Cool Request
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

package com.cool.request.view.main;

import com.cool.request.components.http.net.HTTPResponseBody;
import com.cool.request.components.http.net.RequestContext;
import com.intellij.openapi.progress.ProgressIndicator;

/**
 * 请求发起和结束事件监听器
 */
public interface HTTPEventListener {
    /**
     * 开始发送
     */
    public default void beginSend(RequestContext requestContext, ProgressIndicator progressIndicator) {
    }

    /**
     * 结束发送
     */
    public default void endSend(RequestContext requestContext, HTTPResponseBody httpResponseBody, ProgressIndicator progressIndicator) {
    }

}
