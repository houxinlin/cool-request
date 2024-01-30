package com.cool.request.lib.openapi;

import com.cool.request.common.bean.components.controller.Controller;
import com.cool.request.common.exception.ClassNotFoundException;
import com.cool.request.component.http.net.FormDataInfo;
import com.cool.request.lib.springmvc.*;
import com.cool.request.utils.PsiUtils;
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
import com.intellij.psi.PsiClass;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OpenApiWithAutoParameter {
    public static OpenApiBuilder apply(OpenApiBuilder openApiBuilder, Project project, Controller controller) {

        SpringMvcRequestMapping mvcRequestMapping = new SpringMvcRequestMapping();
        HttpRequestInfo httpRequestInfo = mvcRequestMapping.getHttpRequestInfo(project, controller);

        PsiClass psiClass = PsiUtils.findClassByName(project, controller.getModuleName(), controller.getSimpleClassName());
        if (psiClass == null) {
            throw new ClassNotFoundException(controller.getSimpleClassName());
        }

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
        GuessBody requestBody = httpRequestInfo.getRequestBody();
        if (requestBody instanceof JSONObjectGuessBody) {
            PropertiesBuilder propertiesBuilder = new PropertiesBuilder();
            buildProperties(propertiesBuilder, ((JSONObjectGuessBody) requestBody).getJson());
            openApiBuilder.setRequestBody(new OpenApiApplicationJSONBodyNode(propertiesBuilder.object()));
        }
        return openApiBuilder;
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
