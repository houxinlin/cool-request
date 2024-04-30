/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * ReflexRequestCallMethod.java is part of Cool Request
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

package com.cool.request.components.http.net;

import com.cool.request.components.http.ExceptionInvokeResponseModel;
import com.cool.request.components.http.HTTPResponseManager;
import com.cool.request.components.http.KeyValue;
import com.cool.request.components.http.ReflexHttpRequestParamAdapterBody;
import com.cool.request.components.http.net.request.DynamicReflexHttpRequestParam;
import com.cool.request.components.http.net.request.HttpRequestParamUtils;
import com.cool.request.components.http.response.InvokeResponseModel;
import com.cool.request.lib.springmvc.BinaryBody;
import com.cool.request.lib.springmvc.Body;
import com.cool.request.lib.springmvc.EmptyBody;
import com.cool.request.lib.springmvc.FormBody;
import com.cool.request.rmi.RMIFactory;
import com.cool.request.rmi.starter.ICoolRequestStarterRMI;
import com.cool.request.utils.Base64Utils;
import com.cool.request.utils.StringUtils;
import com.cool.request.utils.UrlUtils;
import com.cool.request.view.tool.UserProjectManager;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReflexRequestCallMethod extends BasicReflexControllerRequestCallMethod {
    private final UserProjectManager userProjectManager;
    private final DynamicReflexHttpRequestParam reflexHttpRequestParam;
    private final Map<RequestContext, Thread> waitResponseThread;

    public ReflexRequestCallMethod(DynamicReflexHttpRequestParam reflexHttpRequestParam,
                                   Map<RequestContext, Thread> waitResponseThread,
                                   UserProjectManager userProjectManager) {
        super(reflexHttpRequestParam);
        this.reflexHttpRequestParam = reflexHttpRequestParam;
        this.waitResponseThread = waitResponseThread;
        this.userProjectManager = userProjectManager;
    }

    @Override
    public void invoke(RequestContext requestContext) {
        List<KeyValue> newHeaders = reflexHttpRequestParam.getHeaders().stream()
                .filter(keyValue -> !StringUtils.isEmpty(keyValue.getKey())).collect(Collectors.toList());
        ReflexHttpRequestParamAdapterBody reflexHttpRequestParamAdapter = ReflexHttpRequestParamAdapterBody
                .ReflexHttpRequestParamAdapterBuilder.aReflexHttpRequestParamAdapter()
                .withUrl(reflexHttpRequestParam.getUrl())
                .withContentType(HttpRequestParamUtils.getContentType(reflexHttpRequestParam, MediaTypes.TEXT))
                .withUseInterceptor(reflexHttpRequestParam.isUseInterceptor())
                .withUseProxyObject(reflexHttpRequestParam.isUseProxyObject())
                .withUserFilter(reflexHttpRequestParam.isUserFilter())
                .withHeaders(reflexHttpRequestParam.getHeaders())
                .withMethod(reflexHttpRequestParam.getMethod().toString())
                .build();

        reflexHttpRequestParamAdapter.setAttachData(reflexHttpRequestParam.getAttachData());
        reflexHttpRequestParamAdapter.setBody("");
        Body body = reflexHttpRequestParam.getBody();
        if (body != null && !(body instanceof EmptyBody)) {
            String contentType = HttpRequestParamUtils.getContentType(reflexHttpRequestParam, null);
            if (contentType == null) {
                reflexHttpRequestParamAdapter.setContentType(body.getMediaType());
            }
        }

        if (body instanceof FormBody) {
            reflexHttpRequestParamAdapter.setFormData(((FormBody) body).getData());
        } else if (body instanceof BinaryBody) {
            reflexHttpRequestParamAdapter.setBody(((BinaryBody) body).getSelectFile());
        } else {
            if (body != null && !(body instanceof EmptyBody)) {
                reflexHttpRequestParamAdapter.setBody(new String(body.contentConversion(), StandardCharsets.UTF_8));
            }
        }

        // 查找远程对象
        try {
            int port = userProjectManager.getRMIPortByProjectPort(UrlUtils.getPort(reflexHttpRequestParam.getUrl()));

            if (port < 0) {
                port = userProjectManager.getRMIPortByProjectPort(requestContext.getController().getServerPort());
            }
            if (port < 0) {
                port = userProjectManager.getRMIPortByProjectPort(userProjectManager.findControllerServerPort(requestContext.getController()));
            }
            if (port > 0) {
                ICoolRequestStarterRMI coolRequestStarterRMI = RMIFactory.getStarterRMI(port);
                requestContext.setBeginTimeMillis(System.currentTimeMillis());

                InvokeResponseModel invokeResponseModel = coolRequestStarterRMI.invokeController(reflexHttpRequestParamAdapter);
                if (invokeResponseModel == null) {
                    invokeResponseModel = new ExceptionInvokeResponseModel(reflexHttpRequestParamAdapter.getId(), new IllegalArgumentException(""));
                }
                if (!waitResponseThread.containsKey(requestContext)) {
                    return;
                }
                HTTPResponseBody httpResponseBody = new HTTPResponseBody();
                httpResponseBody.setHeader(invokeResponseModel.getHeader());
                httpResponseBody.setCode(invokeResponseModel.getCode());
                byte[] responseBody = Base64Utils.decode(invokeResponseModel.getBaseBodyData());
                httpResponseBody.setSize(responseBody.length);
                responseBody = HTTPResponseManager.getInstance(userProjectManager.getProject())
                        .bodyConverter(responseBody, new HTTPHeader(httpResponseBody.getHeader()));
                if (responseBody != null) {
                    httpResponseBody.setBase64BodyData(Base64Utils.encodeToString(responseBody));
                }
                requestContext.endSend(httpResponseBody);
                //通知全局的监听器
                HTTPResponseManager.getInstance(userProjectManager.getProject()).onHTTPResponse(httpResponseBody);
                return;
            }
            throw new IllegalArgumentException("Not Found RMI Port");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
