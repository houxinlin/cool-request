/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * IRequestParamManager.java is part of Cool Request
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

import com.cool.request.common.bean.BeanInvokeSetting;
import com.cool.request.components.http.Controller;
import com.cool.request.components.http.FormDataInfo;
import com.cool.request.components.http.net.HTTPResponseBody;
import com.cool.request.components.http.net.HttpMethod;
import com.cool.request.components.http.KeyValue;
import com.cool.request.components.http.net.MediaType;
import com.cool.request.components.http.net.RequestContext;
import com.cool.request.lib.springmvc.RequestCache;
import com.cool.request.view.page.ScriptLogPage;
import com.cool.request.view.tool.Provider;
import com.intellij.openapi.progress.ProgressIndicator;

import java.util.List;

public interface IRequestParamManager extends HTTPParamApply, Provider {
    public boolean isAvailable();

    public String getUrl();

    public BeanInvokeSetting getBeanInvokeSetting();

    public HttpMethod getHttpMethod();

    public int getInvokeHttpMethod();

    public List<KeyValue> getHttpHeader();

    public List<KeyValue> getUrlParam();

    public List<FormDataInfo> getFormData();

    public List<KeyValue> getUrlencodedBody();

    public String getRequestBody();

    public MediaType getRequestBodyType();

    public void switchRequestBodyType(MediaType type);

    public void setUrl(String url);

    public void setHttpMethod(HttpMethod method);

    public void setInvokeHttpMethod(int index);

    public void setHttpHeader(List<KeyValue> value);

    public void setUrlParam(List<KeyValue> value);

    public void setPathParam(List<KeyValue> value);

    public void setFormData(List<FormDataInfo> value);

    public void setUrlencodedBody(List<KeyValue> value);

    public void setRequestBody(String type, String body);

    public String getRequestScript();

    public String getResponseScript();

    public int getInvokeModelIndex();

    public boolean isReflexRequest();

    public Controller getCurrentController();

    public String getContentTypeFromHeader();

    public void importCurl(String curl);

    public void restParam();

    List<KeyValue> getPathParam();

    public void saveAsCustomController();

    public RequestCache createRequestCache();

    public ScriptLogPage getScriptLogPage();

    public void beginSend(RequestContext requestContext, ProgressIndicator progressIndicator);

    public void endSend(RequestContext requestContext, HTTPResponseBody httpResponseBody, ProgressIndicator progressIndicator);

}
