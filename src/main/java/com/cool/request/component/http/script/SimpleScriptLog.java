package com.cool.request.component.http.script;

import com.cool.request.common.cache.ComponentCacheManager;
import com.cool.request.component.http.net.RequestContext;
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
