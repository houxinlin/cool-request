/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * OpenApiUtils.java is part of Cool Request
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

package com.cool.request.lib.openapi;

import com.cool.request.common.bean.RequestEnvironment;
import com.cool.request.common.cache.CacheStorageService;
import com.cool.request.common.cache.ComponentCacheManager;
import com.cool.request.components.CoolRequestContext;
import com.cool.request.components.http.Controller;
import com.cool.request.components.http.FormDataInfo;
import com.cool.request.components.http.KeyValue;
import com.cool.request.components.http.net.HTTPResponseBody;
import com.cool.request.components.http.net.HttpMethod;
import com.cool.request.lib.openapi.media.*;
import com.cool.request.lib.springmvc.*;
import com.cool.request.lib.springmvc.param.ResponseBodySpeculate;
import com.cool.request.lib.springmvc.utils.ParamUtils;
import com.cool.request.scan.Scans;
import com.cool.request.scan.spring.SpringMvcControllerConverter;
import com.cool.request.scan.swagger.SwaggerMethodDescriptionParse;
import com.cool.request.utils.Base64Utils;
import com.cool.request.utils.GsonUtils;
import com.cool.request.utils.StringUtils;
import com.cool.request.utils.param.CacheParameterProvider;
import com.cool.request.utils.param.GuessParameterProvider;
import com.cool.request.utils.param.HTTPParameterProvider;
import com.cool.request.utils.param.PanelParameterProvider;
import com.cool.request.view.main.IRequestParamManager;
import com.cool.request.view.tool.provider.RequestEnvironmentProvideImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiMethod;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class OpenApiUtils {


    private static HTTPParameterProvider getHTTPParameterProvider(Project project, Controller controller) {
        //生成的参数依靠有没有缓存来判断，如果有缓存，则带代表用户可能使用自己正确的参数进行过请求，则优先使用
        //自动推测的参数可能不正确
        HTTPParameterProvider httpParameterProvider = null;
        IRequestParamManager httpRequestParamPanel = CoolRequestContext.getInstance(project)
                .getMainBottomHTTPContainer()
                .getMainBottomHttpInvokeViewPanel()
                .getHttpRequestParamPanel();

        Controller cureentSelectedController = httpRequestParamPanel.getCurrentController();
        if (cureentSelectedController != null && StringUtils.isEqualsIgnoreCase(cureentSelectedController.getId(), controller.getId())) {
            //先从主窗口拿去数据
            httpParameterProvider = new PanelParameterProvider(httpRequestParamPanel);
        } else {
            RequestCache cache = ComponentCacheManager.getRequestParamCache(controller.getId());
            if (cache != null) {
                httpParameterProvider = new CacheParameterProvider();
            }
        }
        if (httpParameterProvider == null) httpParameterProvider = new GuessParameterProvider();
        return httpParameterProvider;
    }

    private static RequestEnvironment getRequestEnvironment(Project project) {
        return RequestEnvironmentProvideImpl.getInstance(project).getSelectRequestEnvironment();
    }

    private static List<Parameter> buildParameter(Project project, Controller controller) {
        return applyParam(
                getHTTPParameterProvider(project, controller),
                controller, getRequestEnvironment(project),
                project);
    }

    private static Schema<?> getSchema(KeyValue keyValue) {
        if (StringUtils.isEmpty(keyValue.getValueType())) return new StringSchema();
        if (ParamUtils.isFloat(keyValue.getValueType())) return new NumberSchema();
        if (ParamUtils.isBoolean(keyValue.getValueType())) return new BooleanSchema();
        if (ParamUtils.isNumber(keyValue.getValueType())) return new NumberSchema();
        return new StringSchema();
    }

    private static List<Parameter> applyParam(
            HTTPParameterProvider httpParameterProvider,
            Controller controller,
            RequestEnvironment requestEnvironment, Project project) {
        List<Parameter> result = new ArrayList<>();

        //url参数
        for (KeyValue urlParam : Optional.ofNullable(httpParameterProvider.getUrlParam(project, controller, requestEnvironment)).orElse(new ArrayList<>())) {
            result.add(new QueryParameter()
                    .name(urlParam.getKey())
                    .schema(getSchema(urlParam))
                    .description(urlParam.getDescribe()));
        }
        //请求头
        for (KeyValue header : Optional.ofNullable(httpParameterProvider.getHeader(project, controller, requestEnvironment)).orElse(new ArrayList<>())) {
            result.add(new QueryParameter()
                    .name(header.getKey())
                    .schema(getSchema(header))
                    .description(header.getDescribe()));
        }
        return result;
    }

    private static RequestBody createRequestBody(Project project, Controller controller) {

        Body requestBody = getHTTPParameterProvider(project, controller).getBody(project, controller, getRequestEnvironment(project));
        //表单
        if (requestBody instanceof FormBody) {
            ObjectSchema objectSchema = new ObjectSchema();
            for (FormDataInfo formDataInfo : Optional.ofNullable(((FormBody) requestBody).getData()).orElse(new ArrayList<>())) {
                if ("file".equalsIgnoreCase(formDataInfo.getType())) {
                    objectSchema.addProperty(formDataInfo.getName(), new FileSchema());
                } else {
                    objectSchema.addProperty(formDataInfo.getName(), new StringSchema());
                }
            }
            return new RequestBody().content(new Content().addMediaType("multipart/form-data",
                    new MediaType().schema(objectSchema)));
        }

        //from url
        if (requestBody instanceof FormUrlBody) {
            ObjectSchema objectSchema = new ObjectSchema();
            List<KeyValue> urlencodedBody = Optional.ofNullable(((FormUrlBody) requestBody).getData()).orElse(new ArrayList<>());
            for (KeyValue keyValue : urlencodedBody) {
                if (ParamUtils.isNumber(keyValue.getValueType())) {
                    objectSchema.addProperty(keyValue.getKey(), new NumberSchema());
                } else {
                    objectSchema.addProperty(keyValue.getKey(), new StringSchema());
                }
            }
            return new RequestBody()
                    .content(new Content().addMediaType("application/x-www-form-urlencoded", new MediaType().schema(objectSchema)));
        }
        //json
        if (requestBody instanceof JSONBody) {
            String value = ((JSONBody) requestBody).getValue();
            Map<String, Object> map = GsonUtils.toMap(value);
            ObjectSchema objectSchema = new ObjectSchema();
            buildJSONSchema(map, objectSchema);
            return new RequestBody()
                    .content(new Content().addMediaType("application/json", new MediaType().schema(objectSchema)));
        }

        return null;
    }

    private static void buildJSONSchema(Map<String, ?> map, ObjectSchema source) {
        map.forEach((key, object) -> {
            if (object == null) {
                source.addProperty(key, new StringSchema());
                return;
            }
            if (object instanceof Map) {
                ObjectSchema objectSchema = new ObjectSchema();
                source.addProperty(key, objectSchema);
                buildJSONSchema(((Map<String, ?>) object), objectSchema);
            } else if (ParamUtils.isNumber(object.getClass().getName())) {
                source.addProperty(key, new NumberSchema());
            } else if (ParamUtils.isBoolean(object.getClass().getName())) {
                source.addProperty(key, new BooleanSchema());
            } else if (ParamUtils.isFloat(object.getClass().getName())) {
                source.addProperty(key, new NumberSchema());
            } else {
                source.addProperty(key, new StringSchema());
            }
        });
    }

    private static ApiResponses createResponseExample(Project project, Controller controller) {
        //response example body
        //设置响应,直接尝试转化为json
        CacheStorageService service = ApplicationManager.getApplication().getService(CacheStorageService.class);
        HTTPResponseBody responseCache = service.getResponseCache(controller.getId());
        if (responseCache != null) {
            byte[] response = Base64Utils.decode(responseCache.getBase64BodyData());
            if (response != null) {
                String resposneBodyString = new String(response, StandardCharsets.UTF_8);
                if (GsonUtils.isObject(resposneBodyString)) {
                    Map<String, Object> map = GsonUtils.toMap(resposneBodyString);
                    ObjectSchema objectSchema = new ObjectSchema();
                    buildJSONSchema(map, objectSchema);
                    return new ApiResponses().addApiResponse("Success",
                            new ApiResponse().content(new Content().addMediaType("application/json", new MediaType().schema(objectSchema))));

                } else if (GsonUtils.isArray(resposneBodyString)) {
                    List<Map<String, Object>> listMap = GsonUtils.toListMap(resposneBodyString);

                    if (!listMap.isEmpty()) {
                        ObjectSchema objectSchema = new ObjectSchema();
                        buildJSONSchema(listMap.get(0), objectSchema);
                        return new ApiResponses().addApiResponse("Success",
                                new ApiResponse().content(new Content()
                                        .addMediaType("application/json", new MediaType().schema(objectSchema))));
                    }
                }
            }
        }
        PsiMethod psiMethod = new SpringMvcControllerConverter().controllerToPsiMethod(project, controller);
        if (psiMethod != null) {
            ResponseBodySpeculate responseBodySpeculate = new ResponseBodySpeculate();
            HttpRequestInfo httpRequestInfo = new HttpRequestInfo();
            responseBodySpeculate.set(psiMethod, httpRequestInfo);
            GuessBody guessBody = httpRequestInfo.getResponseBody();
            if (guessBody instanceof JSONObjectGuessBody) {
                Map<String, Object> json = ((JSONObjectGuessBody) guessBody).getJson();
                if (json != null) {
                    ObjectSchema objectSchema = new ObjectSchema();
                    buildJSONSchema(json, objectSchema);
                    return new ApiResponses().addApiResponse("Success",
                            new ApiResponse().content(new Content().addMediaType("application/json", new MediaType().schema(objectSchema))));
                }
            }
        }
        return null;
    }

    private static PathItem createGetPathItem(Project project, Controller controller) {
        RequestEnvironment requestEnvironment = getRequestEnvironment(project);
        Operation operation = new Operation()
                .parameters(buildParameter(project, controller));

        PsiMethod psiMethod = Scans.getInstance(project).controllerToPsiMethod(project, controller);

        MethodDescription methodDescription = new SwaggerMethodDescriptionParse().parse(psiMethod);

        operation.requestBody(createRequestBody(project, controller));
        operation.setResponses(createResponseExample(project, controller));
        HTTPParameterProvider httpParameterProvider = getHTTPParameterProvider(project, controller);

        PathItem pathItem = new PathItem()
                .summary(StringUtils.isEmpty(methodDescription.getSummary()) ? controller.getUrl() : methodDescription.getSummary())
                .description(StringUtils.isEmpty(methodDescription.getDescription()) ? controller.getUrl() : methodDescription.getDescription());
        if (httpParameterProvider.getHttpMethod(project, controller, requestEnvironment) == HttpMethod.GET) {
            pathItem.get(operation);
        }
        if (httpParameterProvider.getHttpMethod(project, controller, requestEnvironment) == HttpMethod.POST) {
            pathItem.post(operation);
        }
        if (httpParameterProvider.getHttpMethod(project, controller, requestEnvironment) == HttpMethod.DELETE) {
            pathItem.delete(operation);
        }
        if (httpParameterProvider.getHttpMethod(project, controller, requestEnvironment) == HttpMethod.TRACE) {
            pathItem.trace(operation);
        }
        if (httpParameterProvider.getHttpMethod(project, controller, requestEnvironment) == HttpMethod.HEAD) {
            pathItem.head(operation);
        }
        if (httpParameterProvider.getHttpMethod(project, controller, requestEnvironment) == HttpMethod.PATCH) {
            pathItem.patch(operation);
        }
        if (httpParameterProvider.getHttpMethod(project, controller, requestEnvironment) == HttpMethod.PUT) {
            pathItem.put(operation);
        }
        if (httpParameterProvider.getHttpMethod(project, controller, requestEnvironment) == HttpMethod.OPTIONS) {
            pathItem.options(operation);
        }
        return pathItem;
    }

    public static String toOpenApiJson(Project project, List<Controller> controllers) {
        OpenAPI openAPI = new OpenAPI();
        for (Controller controller : controllers) {
            openAPI.path(controller.getUrl(), createGetPathItem(project, controller));
        }
        try {
            ObjectMapper objectMapper = Json31.mapper();
            objectMapper.writerFor(OpenAPI.class).writeValueAsString(openAPI);
            return Json31.converterMapper().writeValueAsString(openAPI);
        } catch (JsonProcessingException e) {

        }
        return "";
    }

}
