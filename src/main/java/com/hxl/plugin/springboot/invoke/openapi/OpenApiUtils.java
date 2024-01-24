package com.hxl.plugin.springboot.invoke.openapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hxl.plugin.springboot.invoke.Constant;
import com.hxl.plugin.springboot.invoke.bean.EmptyEnvironment;
import com.hxl.plugin.springboot.invoke.bean.components.controller.Controller;
import com.hxl.plugin.springboot.invoke.net.FormDataInfo;
import com.hxl.plugin.springboot.invoke.net.KeyValue;
import com.hxl.plugin.springboot.invoke.springmvc.*;
import com.hxl.plugin.springboot.invoke.utils.IPUtils;
import com.hxl.plugin.springboot.invoke.utils.PsiUtils;
import com.hxl.plugin.springboot.invoke.utils.RequestParamCacheManager;
import com.hxl.plugin.springboot.invoke.utils.StringUtils;
import com.hxl.plugin.springboot.invoke.utils.exception.ClassNotFoundException;
import com.hxl.plugin.springboot.invoke.utils.exception.MethodNotFoundException;
import com.hxl.plugin.springboot.invoke.view.main.RequestEnvironmentProvide;
import com.hxl.plugin.springboot.invoke.view.dialog.IpSelectionDialog;
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
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.*;
import java.util.stream.Collectors;

public class OpenApiUtils {
    private static OpenApiBuilder generatorOpenApiBuilder(Project project, Controller controller) {
        return generatorOpenApiBuilder(project, controller, true);
    }

    private static OpenApiBuilder generatorOpenApiBuilder(Project project, Controller controller, boolean includeHost) {
        String ipAddress = "localhost";
        List<String> availableIpAddresses = IPUtils.getAvailableIpAddresses();
        if (availableIpAddresses.size() == 1) {
            ipAddress = availableIpAddresses.get(0);
        }
        if (availableIpAddresses.size() > 1) {
            IpSelectionDialog dialog = new IpSelectionDialog(null, availableIpAddresses);
            dialog.show();
            if (dialog.getExitCode() == DialogWrapper.OK_EXIT_CODE) {
                String selectedIpAddress = dialog.getSelectedIpAddress();
                if (selectedIpAddress != null) {
                    ipAddress = selectedIpAddress;
                }
            }
        }

        String base = includeHost ?
                "http://" + ipAddress + ":" + controller.getServerPort() + controller.getContextPath() :
                controller.getContextPath();
        String url = StringUtils.joinUrlPath(base, controller.getUrl());
        RequestEnvironmentProvide requestEnvironmentProvide = project.getUserData(Constant.RequestEnvironmentProvideKey);
        if (includeHost && !(requestEnvironmentProvide.getSelectRequestEnvironment() instanceof EmptyEnvironment)) {
            url = requestEnvironmentProvide.applyUrl(controller);
        } else if (!includeHost && !(requestEnvironmentProvide.getSelectRequestEnvironment() instanceof EmptyEnvironment)) {
            String fullUrl = StringUtils.getFullUrl(controller);
            url = StringUtils.joinUrlPath(StringUtils.removeHostFromUrl(requestEnvironmentProvide.getSelectRequestEnvironment().getHostAddress()), fullUrl);
        }

        SpringMvcRequestMapping mvcRequestMapping = new SpringMvcRequestMapping();
        HttpRequestInfo httpRequestInfo = mvcRequestMapping.getHttpRequestInfo(project, controller);

        PsiClass psiClass = PsiUtils.findClassByName(project, controller.getModuleName(), controller.getSimpleClassName());
        if (psiClass == null) {
            throw new ClassNotFoundException(controller.getSimpleClassName());
        }
        PsiMethod httpMethodInClass = PsiUtils.findHttpMethodInClass(psiClass,
                controller.getMethodName(),
                controller.getHttpMethod(),
                controller.getParamClassList(),
                controller.getUrl());

        if (httpMethodInClass == null) {
            throw new MethodNotFoundException(controller.getSimpleClassName() + "." + controller.getMethodName());
        }

        MethodDescription methodDescription =
                ParameterAnnotationDescriptionUtils.getMethodDescription(httpMethodInClass);
        HttpMethod httpMethod;
        try {
            httpMethod = HttpMethod.valueOf(controller.getHttpMethod().toLowerCase());
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
        GuessBody requestBody = httpRequestInfo.getRequestBody();
        if (requestBody instanceof JSONObjectBody) {
            PropertiesBuilder propertiesBuilder = new PropertiesBuilder();
            buildProperties(propertiesBuilder, ((JSONObjectBody) requestBody).getJson());
            openApiBuilder.setRequestBody(new OpenApiApplicationJSONBodyNode(propertiesBuilder.object()));
        }
        return openApiBuilder;
    }

    public static String toCurl(Project project, Controller controller) {
        RequestCache requestCache = RequestParamCacheManager.getCache(controller.getId());
        if (requestCache == null) {
            return generatorOpenApiBuilder(project, controller).toCurl(s -> "", s -> "", s -> "", () -> "");
        }
        return generatorOpenApiBuilder(project, controller).toCurl(s -> {
            if (requestCache.getHeaders() != null) {
                for (KeyValue header : requestCache.getHeaders()) {
                    if (header.getKey().equalsIgnoreCase(s)) {
                        return header.getValue();
                    }
                }
            }
            return "";
        }, s -> {
            if (requestCache.getUrlParams() != null) {
                for (KeyValue param : requestCache.getUrlParams()) {
                    if (param.getKey().equalsIgnoreCase(s)) {
                        return param.getValue();
                    }
                }
            }
            return "";
        }, s -> {
            if (requestCache.getFormDataInfos() == null) {
                return null;
            }
            for (FormDataInfo formDataInfo : requestCache.getFormDataInfos()) {
                if (formDataInfo.getName().equalsIgnoreCase(s)) {
                    return formDataInfo.getValue();
                }
            }
            return null;
        }, requestCache::getRequestBody);
    }

    public static String toOpenApiJson(Project project, List<Controller> controllers) {
        return toOpenApiJson(project, controllers, true);
    }

    public static String toOpenApiJson(Project project, List<Controller> controllers, boolean includeHost) {
        OpenApi openApi = new OpenApi();
        for (Controller controller : controllers) {
            generatorOpenApiBuilder(project, controller, includeHost).addToOpenApi(openApi);
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
