package com.hxl.plugin.springboot.invoke.openapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hxl.plugin.springboot.invoke.model.RequestMappingModel;
import com.hxl.plugin.springboot.invoke.model.SpringMvcRequestMappingSpringInvokeEndpoint;
import com.hxl.plugin.springboot.invoke.net.FormDataInfo;
import com.hxl.plugin.springboot.invoke.net.KeyValue;
import com.hxl.plugin.springboot.invoke.springmvc.*;
import com.hxl.plugin.springboot.invoke.utils.PsiUtils;
import com.hxl.plugin.springboot.invoke.utils.RequestParamCacheManager;
import com.hxl.utils.openapi.HttpMethod;
import com.hxl.utils.openapi.OpenApi;
import com.hxl.utils.openapi.OpenApiBuilder;
import com.hxl.utils.openapi.Type;
import com.hxl.utils.openapi.body.OpenApiApplicationJSONBodyNode;
import com.hxl.utils.openapi.body.OpenApiFormDataRequestBodyNode;
import com.hxl.utils.openapi.body.OpenApiFormUrlencodedBodyNode;
import com.hxl.utils.openapi.parameter.OpenApiHeaderParameter;
import com.hxl.utils.openapi.parameter.OpenApiUrlQueryParameter;
import com.hxl.utils.openapi.properties.ObjectProperties;
import com.hxl.utils.openapi.properties.Properties;
import com.hxl.utils.openapi.properties.PropertiesBuilder;
import com.hxl.utils.openapi.properties.PropertiesUtils;
import com.intellij.openapi.project.Project;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class OpenApiUtils {
    private static OpenApiBuilder generatorOpenApiBuilder(Project project, RequestMappingModel requestMappingModel) {
        return generatorOpenApiBuilder(project, requestMappingModel, true);
    }

    private static OpenApiBuilder generatorOpenApiBuilder(Project project, RequestMappingModel requestMappingModel, boolean includeHost) {
        SpringMvcRequestMappingSpringInvokeEndpoint controller = requestMappingModel.getController();
//        String base = "http://localhost:" + requestMappingModel.getServerPort() + requestMappingModel.getContextPath();
        String base = includeHost ? "http://localhost:" + requestMappingModel.getServerPort() + requestMappingModel.getContextPath() :
                requestMappingModel.getContextPath();
        String url = base + requestMappingModel.getController().getUrl();

        HttpRequestInfo httpRequestInfo = SpringMvcRequestMappingUtils.getHttpRequestInfo(project, requestMappingModel);
        MethodDescription methodDescription =
                ParameterAnnotationDescriptionUtils.getMethodDescription(
                        PsiUtils.findHttpMethodInClass(project, controller));

        HttpMethod httpMethod;
        try {
            httpMethod = HttpMethod.valueOf(requestMappingModel.getController().getHttpMethod().toLowerCase());
        } catch (Exception e) {
            httpMethod = HttpMethod.get;
        }
        OpenApiBuilder openApiBuilder = OpenApiBuilder.create(url, Optional.ofNullable(methodDescription.getSummary()).orElse(url), httpMethod);
        //url参数
        for (RequestParameterDescription urlParam : httpRequestInfo.getUrlParams()) {
            openApiBuilder.addParameter(
                    new OpenApiUrlQueryParameter(urlParam.getName(), urlParam.getDescription(), true, Type.parse(urlParam.getType(), Type.string)));
        }
        //请求头
        for (RequestParameterDescription header : httpRequestInfo.getHeaders()) {
            openApiBuilder.addParameter(new OpenApiHeaderParameter(header.getName(), header.getDescription(), true, Type.parse(header.getType(), Type.string)));
        }
        //表单
        List<FormDataInfo> formDataInfos = httpRequestInfo.getFormDataInfos();
        if (!formDataInfos.isEmpty()) {
            List<Properties> properties = new ArrayList<>();
            for (FormDataInfo formDataInfo : formDataInfos) {
                if ("file".equalsIgnoreCase(formDataInfo.getType())) {
                    properties.add(PropertiesUtils.createFile(formDataInfo.getName(), formDataInfo.getDescription()));
                } else {
                    properties.add(PropertiesUtils.createString(formDataInfo.getName(), formDataInfo.getDescription()));
                }
            }
            openApiBuilder.setRequestBody(new OpenApiFormDataRequestBodyNode(new ObjectProperties(properties)));
        }
        //from url
        List<RequestParameterDescription> urlencodedBody = httpRequestInfo.getUrlencodedBody();
        if (!urlencodedBody.isEmpty()) {
            List<Properties> collect = urlencodedBody.stream()
                    .map(requestParameterDescription1 ->
                            PropertiesUtils.createString(requestParameterDescription1.getName(), requestParameterDescription1.getDescription())).collect(Collectors.toList());
            openApiBuilder.setRequestBody(new OpenApiFormUrlencodedBodyNode(new ObjectProperties(collect)));
        }
        //request body
        Body requestBody = httpRequestInfo.getRequestBody();
        if (requestBody instanceof JSONObjectBody) {
            PropertiesBuilder propertiesBuilder = new PropertiesBuilder();
            buildProperties(propertiesBuilder, ((JSONObjectBody) requestBody).getJson());
            openApiBuilder.setRequestBody(new OpenApiApplicationJSONBodyNode(propertiesBuilder.object()));
        }
        return openApiBuilder;
    }

    public static String toCurl(Project project, RequestMappingModel requestMappingModel) {
        RequestCache requestCache = RequestParamCacheManager.getCache(requestMappingModel);
        if (requestCache == null)
            return generatorOpenApiBuilder(project, requestMappingModel).toCurl(s -> "", s -> "", s -> "", () -> "");
        return generatorOpenApiBuilder(project, requestMappingModel).toCurl(s -> {
            if (requestCache.getHeaders() != null) {
                for (KeyValue header : requestCache.getHeaders()) {
                    if (header.getKey().equalsIgnoreCase(s)) return header.getValue();
                }
            }
            return "";
        }, s -> {
            if (requestCache.getUrlParams() != null) {
                for (KeyValue param : requestCache.getUrlParams()) {
                    if (param.getKey().equalsIgnoreCase(s)) return param.getValue();
                }
            }
            return "";
        }, s -> {
            if (requestCache.getFormDataInfos() == null) return null;
            for (FormDataInfo formDataInfo : requestCache.getFormDataInfos()) {
                if (formDataInfo.getName().equalsIgnoreCase(s)) return formDataInfo.getValue();
            }
            return null;
        }, requestCache::getRequestBody);
    }

    public static String toOpenApiJson(Project project, List<RequestMappingModel> requestMappingModelList) {
        return toOpenApiJson(project, requestMappingModelList, true);
    }

    public static String toOpenApiJson(Project project, List<RequestMappingModel> requestMappingModelList, boolean includeHost) {
        OpenApi openApi = new OpenApi();
        for (RequestMappingModel requestMappingModel : requestMappingModelList) {
            generatorOpenApiBuilder(project, requestMappingModel, includeHost).addToOpenApi(openApi);
        }
        try {
            return new ObjectMapper().writeValueAsString(openApi);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private static void buildProperties(PropertiesBuilder propertiesBuilder, Map<String, Object> json) {
        json.forEach((name, value) -> {
            if (value instanceof Map) {
                Map<String, Object> valueMap = (Map<String, Object>) value;
                propertiesBuilder.addObjectProperties(name, propertiesBuilder1 -> buildProperties(propertiesBuilder1, valueMap), "");
            } else {
                propertiesBuilder.addStringProperties(name, value.toString());
            }
        });
    }
}
