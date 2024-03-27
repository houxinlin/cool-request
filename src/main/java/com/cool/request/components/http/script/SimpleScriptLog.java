/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * SimpleScriptLog.java is part of Cool Request
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

package com.cool.request.components.http.script;

import com.cool.request.common.cache.ComponentCacheManager;
import com.cool.request.components.http.net.RequestContext;
import com.cool.request.lib.springmvc.RequestCache;
import com.cool.request.script.ILog;
import com.cool.request.utils.StringUtils;
import com.cool.request.view.main.IRequestParamManager;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class SimpleScriptLog extends PrintStream implements ILog {
    private RequestContext requestContext;
    private IRequestParamManager requestParamManager;


    public SimpleScriptLog(RequestContext requestContext,
                           IRequestParamManager requestParamManager) {
        super(new ByteArrayOutputStream());
        this.requestContext = requestContext;
        this.requestParamManager = requestParamManager;
    }

    @Override
    public void println(@Nullable String log) {
        //如果当前窗口和请求的窗口是一个api
        storageLog(log + "\n");
        if (StringUtils.isEqualsIgnoreCase(requestContext.getController().getId(), requestContext.getController().getId())) {
            requestParamManager.getScriptLogPage().setLog(requestContext.getLog());
        }
    }

    @Override
    public void print(@Nullable String log) {
        storageLog(log);
        if (StringUtils.isEqualsIgnoreCase(requestContext.getController().getId(), requestContext.getController().getId())) {
            requestParamManager.getScriptLogPage().setLog(requestContext.getLog());
        }
    }

    private void storageLog(@Nullable String log) {
        requestContext.appendLog(log);
        RequestCache cache = ComponentCacheManager.getRequestParamCache(requestContext.getId());
        if (cache != null) {
            cache.setScriptLog(requestContext.getLog());
            ComponentCacheManager.storageRequestCache(requestContext.getId(), cache);
        }
    }

    @Override
    public void clearLog() {
        if (StringUtils.isEqualsIgnoreCase(requestContext.getController().getId(), requestContext.getController().getId())) {
            requestParamManager.getScriptLogPage().clearAllLog();
        }
    }
}
