package com.cool.request.lib.openapi;

import com.cool.request.common.bean.RequestEnvironment;
import com.cool.request.common.bean.components.controller.Controller;
import com.cool.request.common.bean.components.controller.CustomController;
import com.cool.request.common.bean.components.controller.DynamicController;
import com.cool.request.common.bean.components.controller.StaticController;
import com.cool.request.common.cache.CacheStorageService;
import com.cool.request.common.constant.CoolRequestConfigConstant;
import com.cool.request.common.model.InvokeResponseModel;
import com.cool.request.component.http.net.FormDataInfo;
import com.cool.request.component.http.net.KeyValue;
import com.cool.request.lib.springmvc.*;
import com.cool.request.lib.springmvc.param.ResponseBodySpeculate;
import com.cool.request.utils.*;
import com.cool.request.utils.param.CacheParameterProvider;
import com.cool.request.utils.param.GuessParameterProvider;
import com.cool.request.utils.param.HTTPParameterProvider;
import com.cool.request.utils.param.PanelParameterProvider;
import com.cool.request.view.ViewRegister;
import com.cool.request.view.main.MainBottomHTTPResponseView;
import com.cool.request.view.tool.ProviderManager;
import com.cool.request.view.tool.RequestParamCacheManager;
import com.hxl.utils.openapi.HttpMethod;
import com.hxl.utils.openapi.OpenApi;
import com.hxl.utils.openapi.OpenApiBuilder;
import com.hxl.utils.openapi.Type;
import com.hxl.utils.openapi.body.OpenApiApplicationJSONBodyNode;
import com.hxl.utils.openapi.body.OpenApiFormDataRequestBodyNode;
import com.hxl.utils.openapi.body.OpenApiFormUrlencodedBodyNode;
import com.hxl.utils.openapi.parameter.OpenApiHeaderParameter;
import com.hxl.utils.openapi.parameter.OpenApiUrlQueryParameter;
import com.hxl.utils.openapi.properties.*;
import com.hxl.utils.openapi.response.OpenApiResponseDetailNode;
import com.hxl.utils.openapi.response.OpenApiStatusCodeResponse;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class OpenApiUtils {
    //openapi和系统得数据类型转换器
    private static Function<String, String> OPENAPI_DATA_TYPE_ADAPTER = s -> {
        if ("float".equalsIgnoreCase(s) || "double".equalsIgnoreCase(s)) return "number";
        if ("int".equalsIgnoreCase(s) || "long".equalsIgnoreCase(s) || "integer".equalsIgnoreCase(s))
            return "integer";
        if ("boolean".equalsIgnoreCase(s)) return "boolean";
        return "string";
    };

    private static MethodDescription getMethodDescription(Project project, Controller controller) {
        if (controller instanceof CustomController) {
            return new DefaultMethodDescription(controller);
        } else {
            PsiClass psiClass = PsiUtils.findClassByName(project, controller.getModuleName(), controller.getSimpleClassName());
            if (psiClass == null) {
                return new DefaultMethodDescription(controller);
            }
            PsiMethod httpMethodInClass = PsiUtils.findHttpMethodInClass(psiClass,
                    controller.getMethodName(),
                    controller.getHttpMethod(),
                    controller.getParamClassList(),
                    controller.getUrl());
            return ParameterAnnotationDescriptionUtils.getMethodDescription(httpMethodInClass);
        }
    }

    private static OpenApiBuilder generatorOpenApiBuilder(Project project, Controller controller) {

        String url = "";
        if (controller instanceof CustomController) {
            url = StringUtils.removeHostFromUrl(controller.getUrl());
        } else {
            url = ControllerUtils.getFullUrl(controller);
        }

        MethodDescription methodDescription = getMethodDescription(project, controller);
        HttpMethod httpMethod;
        try {
            httpMethod = HttpMethod.valueOf(controller.getHttpMethod().toLowerCase());
        } catch (Exception e) {
            httpMethod = HttpMethod.get;
        }
        OpenApiBuilder openApiBuilder = OpenApiBuilder.create(url, Optional.ofNullable(methodDescription.getSummary()).orElse(url), httpMethod);

        //生成的参数依靠有没有缓存来判断，如果有缓存，则带代表用户可能使用自己正确的参数进行过请求，则优先使用
        //自动推测的参数可能不正确
        Controller cureentSelectedController = ProviderManager.getProvider(ViewRegister.class, project).getView(MainBottomHTTPResponseView.class).getController();

        HTTPParameterProvider httpParameterProvider = null;
        if (cureentSelectedController != null && StringUtils.isEqualsIgnoreCase(cureentSelectedController.getId(), controller.getId())) {
            httpParameterProvider = new PanelParameterProvider();
        } else {
            RequestCache cache = RequestParamCacheManager.getCache(controller.getId());
            if (cache != null) {
                httpParameterProvider = new CacheParameterProvider();
            }
        }
        if (httpParameterProvider == null) httpParameterProvider = new GuessParameterProvider();

        RequestEnvironment selectRequestEnvironment = project.getUserData(CoolRequestConfigConstant.RequestEnvironmentProvideKey).getSelectRequestEnvironment();

        applyParam(openApiBuilder, httpParameterProvider, controller, selectRequestEnvironment, project);
        return openApiBuilder;
    }

    private static void buildProperties(PropertiesBuilder propertiesBuilder, Map<String, Object> json) {
        if (json == null) return;
        json.forEach((name, value) -> {
            if (value instanceof Map) {
                Map<String, Object> valueMap = (Map<String, Object>) value;
                propertiesBuilder.addObjectProperties(name, propertiesBuilder1 -> buildProperties(propertiesBuilder1, valueMap), "");
            } else {
                propertiesBuilder.addProperties(name, "", Type.parse(OPENAPI_DATA_TYPE_ADAPTER.apply(DataTypeUtils.getDataType(value)), Type.string));
            }
        });
    }

    private static void applyParam(OpenApiBuilder openApiBuilder,
                                   HTTPParameterProvider httpParameterProvider,
                                   Controller controller,
                                   RequestEnvironment requestEnvironment, Project project) {

        //url参数
        for (KeyValue urlParam : Optional.ofNullable(httpParameterProvider.getUrlParam(project, controller, requestEnvironment)).orElse(new ArrayList<>())) {
            openApiBuilder.addParameter(
                    new OpenApiUrlQueryParameter(urlParam.getKey(), urlParam.getDescribe(), true,
                            Type.parse(OPENAPI_DATA_TYPE_ADAPTER.apply(urlParam.getValueType()), Type.string)));
        }
        //请求头
        for (KeyValue header : Optional.ofNullable(httpParameterProvider.getHeader(project, controller, requestEnvironment)).orElse(new ArrayList<>())) {
            openApiBuilder.addParameter(new OpenApiHeaderParameter(header.getKey(), header.getDescribe(),
                    true, Type.parse(OPENAPI_DATA_TYPE_ADAPTER.apply(header.getValueType()), Type.string)));
        }

        Body requestBody = httpParameterProvider.getBody(project, controller, requestEnvironment);
        //表单
        if (requestBody instanceof FormBody) {
            List<Properties> properties = new ArrayList<>();
            for (FormDataInfo formDataInfo : Optional.ofNullable(((FormBody) requestBody).getData()).orElse(new ArrayList<>())) {
                if ("file".equalsIgnoreCase(formDataInfo.getType())) {
                    properties.add(PropertiesUtils.createFile(formDataInfo.getName(), formDataInfo.getDescription()));
                } else {
                    properties.add(PropertiesUtils.createString(formDataInfo.getName(), formDataInfo.getDescription()));
                }
            }
            openApiBuilder.setRequestBody(new OpenApiFormDataRequestBodyNode(new ObjectProperties(properties)));
        }

        //from url
        if (requestBody instanceof FormUrlBody) {
            List<KeyValue> urlencodedBody = Optional.ofNullable(((FormUrlBody) requestBody).getData()).orElse(new ArrayList<>());
            List<Properties> collect = urlencodedBody.stream()
                    .map(keyValue ->
                            PropertiesUtils.createString(keyValue.getKey(), keyValue.getDescribe())).collect(Collectors.toList());
            openApiBuilder.setRequestBody(new OpenApiFormUrlencodedBodyNode(new ObjectProperties(collect)));
        }
        //json
        if (requestBody instanceof JSONBody) {
            PropertiesBuilder propertiesBuilder = new PropertiesBuilder();
            buildProperties(propertiesBuilder, GsonUtils.toMap(((JSONBody) requestBody).getValue()));
            openApiBuilder.setRequestBody(new OpenApiApplicationJSONBodyNode(propertiesBuilder.object()));
        }
        //response example body
        PropertiesBuilder responseJsonPropertiesBuilder = new PropertiesBuilder();
        //设置响应,直接尝试转化为json
        CacheStorageService service = ApplicationManager.getApplication().getService(CacheStorageService.class);
        InvokeResponseModel responseCache = service.loadResponseCache(controller.getId());
        OpenApiStatusCodeResponse openApiStatusCodeResponse = null;
        if (responseCache != null) {
            byte[] response = Base64Utils.decode(responseCache.getBase64BodyData());
            if (response != null) {
                String resposneBodyString = new String(response, StandardCharsets.UTF_8);
                if (GsonUtils.isObject(resposneBodyString)) {
                    Map<String, Object> map = GsonUtils.toMap(resposneBodyString);
                    buildProperties(responseJsonPropertiesBuilder, map);

                    openApiStatusCodeResponse = new OpenApiStatusCodeResponse(200, new OpenApiResponseDetailNode("Response Success",
                            "application/json", responseJsonPropertiesBuilder.object(), resposneBodyString));
                } else if (GsonUtils.isArray(resposneBodyString)) {
                    List<Map<String, Object>> listMap = GsonUtils.toListMap(resposneBodyString);

                    if (!listMap.isEmpty()) {
                        buildProperties(responseJsonPropertiesBuilder, listMap.get(0));
                        ArrayProperties properties = responseJsonPropertiesBuilder.array(responseJsonPropertiesBuilder.object());
                        OpenApiResponseDetailNode responseSuccess = new OpenApiResponseDetailNode("Response Success",
                                "application/json", properties, listMap);
                        openApiStatusCodeResponse = new OpenApiStatusCodeResponse(200, responseSuccess);
                    }
                }
                if (openApiStatusCodeResponse != null) {
                    openApiBuilder.setResponse(openApiStatusCodeResponse);
                }
            }
        }
        if (openApiStatusCodeResponse != null) return;
        if (!(controller instanceof StaticController || controller instanceof DynamicController)) return;

        //推测body
        List<PsiMethod> ownerPsiMethod = controller.getOwnerPsiMethod();
        if (ownerPsiMethod == null || ownerPsiMethod.isEmpty()) return;

        ResponseBodySpeculate responseBodySpeculate = new ResponseBodySpeculate();
        HttpRequestInfo httpRequestInfo = new HttpRequestInfo();
        responseBodySpeculate.set(ownerPsiMethod.get(0), httpRequestInfo);
        GuessBody guessBody = httpRequestInfo.getResponseBody();
        if (guessBody instanceof JSONObjectGuessBody) {
            Map<String, Object> json = ((JSONObjectGuessBody) guessBody).getJson();
            if (json != null) {
                responseJsonPropertiesBuilder = new PropertiesBuilder();
                buildProperties(responseJsonPropertiesBuilder, json);
                openApiBuilder.setResponse(new OpenApiStatusCodeResponse(200,
                        new OpenApiResponseDetailNode("Response Success",
                                "application/json", responseJsonPropertiesBuilder.object(),json)));
            }
        }
    }


//    private static String getBasePath(Controller controller) {
//        String ipAddress = "localhost";
//        List<String> availableIpAddresses = IPUtils.getAvailableIpAddresses();
//        availableIpAddresses.add(0, "localhost");
//        if (availableIpAddresses.size() == 1) {
//            ipAddress = availableIpAddresses.get(0);
//        }
//        if (availableIpAddresses.size() > 1) {
//            IpSelectionDialog dialog = new IpSelectionDialog(null, availableIpAddresses);
//            dialog.show();
//            if (dialog.getExitCode() == DialogWrapper.OK_EXIT_CODE) {
//                String selectedIpAddress = dialog.getSelectedIpAddress();
//                if (selectedIpAddress != null) {
//                    ipAddress = selectedIpAddress;
//                }
//            }
//        }
//        return includeHost ?
//                "http://" + ipAddress + ":" + controller.getServerPort() + controller.getContextPath() :
//                controller.getContextPath();
//    }

    public static String toOpenApiJson(Project project, List<Controller> controllers) {
        return toOpenApiJson(project, controllers, false);
    }

    public static String toOpenApiJson(Project project, List<Controller> controllers, boolean includeHost) {
        OpenApi openApi = new OpenApi();
        for (Controller controller : controllers) {
            generatorOpenApiBuilder(project, controller).addToOpenApi(openApi);
        }
        return GsonUtils.toJsonString(openApi);
    }

}
