package com.hxl.plugin.springboot.invoke.openapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hxl.plugin.springboot.invoke.model.RequestMappingModel;
import com.hxl.plugin.springboot.invoke.model.SpringMvcRequestMappingInvokeBean;
import com.hxl.plugin.springboot.invoke.net.FormDataInfo;
import com.hxl.plugin.springboot.invoke.springmvc.*;
import com.hxl.plugin.springboot.invoke.utils.PsiUtils;
import com.hxl.utils.openapi.HttpMethod;
import com.hxl.utils.openapi.OpenApi;
import com.hxl.utils.openapi.OpenApiBuilder;
import com.hxl.utils.openapi.Type;
import com.hxl.utils.openapi.body.OpenApiApplicationJSONBodyNode;
import com.hxl.utils.openapi.body.OpenApiFormDataRequestBodyNode;
import com.hxl.utils.openapi.body.OpenApiFormUrlencodedBodyNode;
import com.hxl.utils.openapi.parameter.OpenApiHeaderParameterNode;
import com.hxl.utils.openapi.parameter.OpenApiUrlQueryParameter;
import com.hxl.utils.openapi.properties.ObjectProperties;
import com.hxl.utils.openapi.properties.Properties;
import com.hxl.utils.openapi.properties.PropertiesBuilder;
import com.hxl.utils.openapi.properties.PropertiesUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class OpenApiUtils {
    public static String toOpenApiJson(List<RequestMappingModel> requestMappingModelList) {
        OpenApi openApi = new OpenApi();
        for (RequestMappingModel requestMappingModel : requestMappingModelList) {
            SpringMvcRequestMappingInvokeBean controller = requestMappingModel.getController();
            HttpRequestInfo httpRequestInfo = SpringMvcRequestMappingUtils.getHttpRequestInfo(requestMappingModel);
            MethodDescription methodDescription = ParameterAnnotationDescriptionUtils.getMethodDescription(PsiUtils.findMethod(controller.getSimpleClassName(), controller.getMethodName()));
            HttpMethod httpMethod;
            try {
                httpMethod = HttpMethod.valueOf(requestMappingModel.getController().getHttpMethod().toLowerCase());
            } catch (Exception e) {
                httpMethod = HttpMethod.get;
            }
            OpenApiBuilder openApiBuilder = OpenApiBuilder.create(controller.getUrl(), Optional.ofNullable(methodDescription.getSummary()).orElse(controller.getUrl()), httpMethod);
            //url参数
            for (RequestParameterDescription urlParam : httpRequestInfo.getUrlParams()) {
                openApiBuilder.addParameter(
                        new OpenApiUrlQueryParameter(urlParam.getName(), urlParam.getDescription(), true, Type.parse(urlParam.getName(), Type.string)));
            }
            //请求头
            for (RequestParameterDescription header : httpRequestInfo.getHeaders()) {
                openApiBuilder.addParameter(new OpenApiHeaderParameterNode(header.getName(), header.getDescription(), true, Type.parse(header.getName(), Type.string)));
            }
            //表单
            List<FormDataInfo> formDataInfos = httpRequestInfo.getFormDataInfos();
            if (!formDataInfos.isEmpty()) {
                List<Properties> properties = new ArrayList<>();
                for (FormDataInfo formDataInfo : formDataInfos) {
                    properties.add(PropertiesUtils.createFile(formDataInfo.getName(), formDataInfo.getDescription()));
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
            openApiBuilder.addToOpenApi(openApi);
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
