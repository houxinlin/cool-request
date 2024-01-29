package com.cool.request.lib.openapi;

import com.cool.request.common.bean.components.controller.Controller;
import com.cool.request.component.http.net.FormDataInfo;
import com.cool.request.component.http.net.KeyValue;
import com.cool.request.component.http.net.MediaTypes;
import com.cool.request.lib.springmvc.RequestCache;
import com.cool.request.utils.CollectionUtils;
import com.cool.request.view.tool.RequestParamCacheManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
import java.util.stream.Collectors;

public class OpenApiWithUserParameter extends GlobalParameter {

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

    public static void apply(OpenApiBuilder openApiBuilder, Project project, Controller controller) {
        //url参数
        RequestCache cache = RequestParamCacheManager.getCache(controller.getId());
        for (KeyValue urlParam : cache.getUrlParams()) {
            openApiBuilder.addParameter(
                    new OpenApiUrlQueryParameter(urlParam.getKey(), "", true, Type.string));
        }
        //请求头
        for (KeyValue header : CollectionUtils.uniqueMerge(cache.getHeaders(), getGlobalHeader(project))) {
            openApiBuilder.addParameter(new OpenApiHeaderParameter(header.getKey(), "", true, Type.string));
        }
        //form data
        for (FormDataInfo formDataInfo : CollectionUtils.uniqueMerge(cache.getFormDataInfos(), getGlobalFormData(project))) {
            List<Properties> properties = new ArrayList<>();
            if ("file".equalsIgnoreCase(formDataInfo.getType())) {
                properties.add(PropertiesUtils.createFile(formDataInfo.getName(), formDataInfo.getDescription()));
            } else {
                properties.add(PropertiesUtils.createString(formDataInfo.getName(), formDataInfo.getDescription()));
            }
            openApiBuilder.setRequestBody(new OpenApiFormDataRequestBodyNode(new ObjectProperties(properties)));
            return;
        }
        //from url
        List<KeyValue> urlencodedBody = cache.getUrlencodedBody();
        if (urlencodedBody!=null && !urlencodedBody.isEmpty()){
            List<Properties> collect = urlencodedBody.stream()
                    .map(requestParameterDescription1 ->
                            PropertiesUtils.createString(requestParameterDescription1.getKey(), "")).collect(Collectors.toList());
            openApiBuilder.setRequestBody(new OpenApiFormUrlencodedBodyNode(new ObjectProperties(collect)));
            return;
        }
        //request body
        String requestBody = cache.getRequestBody();

        //json类型
        if (MediaTypes.APPLICATION_JSON.equalsIgnoreCase(cache.getRequestBodyType())) {
            java.lang.reflect.Type type = new TypeToken<Map<String, Object>>() {
            }.getType();
            Gson gson = new Gson();
            Map<String, Object> resultMap = gson.fromJson(requestBody, type);
            PropertiesBuilder propertiesBuilder = new PropertiesBuilder();
            buildProperties(propertiesBuilder, resultMap);
            openApiBuilder.setRequestBody(new OpenApiApplicationJSONBodyNode(propertiesBuilder.object()));
        }
    }
}
