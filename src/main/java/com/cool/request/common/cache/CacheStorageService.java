/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * CacheStorageService.java is part of Cool Request
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

package com.cool.request.common.cache;

import com.cool.request.components.http.net.HTTPResponseBody;
import com.cool.request.lib.springmvc.RequestCache;

public interface CacheStorageService {
    public void storageRequestCache(String id, RequestCache requestCache);

    public void removeRequestCache(String id);

    public RequestCache getRequestCache(String id);

    public void storageResponseCache(String requestId, HTTPResponseBody httpResponseBody);

    public void removeResponseCache(String id);

    public HTTPResponseBody getResponseCache(String requestId);

    public void removeAllCache();

}
