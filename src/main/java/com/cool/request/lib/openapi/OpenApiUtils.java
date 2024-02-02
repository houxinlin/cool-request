package com.cool.request.lib.openapi;

import com.cool.request.common.bean.EmptyEnvironment;
import com.cool.request.common.bean.components.controller.Controller;
import com.cool.request.common.constant.CoolRequestConfigConstant;
import com.cool.request.common.exception.ClassNotFoundException;
import com.cool.request.common.exception.MethodNotFoundException;
import com.cool.request.lib.springmvc.MethodDescription;
import com.cool.request.lib.springmvc.ParameterAnnotationDescriptionUtils;
import com.cool.request.lib.springmvc.RequestCache;
import com.cool.request.utils.GsonUtils;
import com.cool.request.utils.IPUtils;
import com.cool.request.utils.PsiUtils;
import com.cool.request.utils.StringUtils;
import com.cool.request.view.dialog.IpSelectionDialog;
import com.cool.request.view.main.RequestEnvironmentProvide;
import com.cool.request.view.tool.RequestParamCacheManager;
import com.hxl.utils.openapi.HttpMethod;
import com.hxl.utils.openapi.OpenApi;
import com.hxl.utils.openapi.OpenApiBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;

import java.util.List;
import java.util.Optional;

public class OpenApiUtils {
    private static OpenApiBuilder generatorOpenApiBuilder(Project project, Controller controller) {
        return generatorOpenApiBuilder(project, controller, true);
    }

    private static OpenApiBuilder generatorOpenApiBuilder(Project project, Controller controller, boolean includeHost) {
        String base = getBasePath(controller, includeHost);

        String url = StringUtils.joinUrlPath(base, controller.getUrl());

        RequestEnvironmentProvide requestEnvironmentProvide = project.getUserData(CoolRequestConfigConstant.RequestEnvironmentProvideKey);
        if (includeHost && !(requestEnvironmentProvide.getSelectRequestEnvironment() instanceof EmptyEnvironment)) {
            url = requestEnvironmentProvide.applyUrl(controller);
        } else if (!includeHost && !(requestEnvironmentProvide.getSelectRequestEnvironment() instanceof EmptyEnvironment)) {
            String fullUrl = StringUtils.getFullUrl(controller);
            url = StringUtils.joinUrlPath(StringUtils.removeHostFromUrl(requestEnvironmentProvide.getSelectRequestEnvironment().getHostAddress()), fullUrl);
        }


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

        //生成的参数依靠有没有缓存来判断，如果有缓存，则带代表用户可能使用自己正确的参数进行过请求，则优先使用
        //自动推测的参数可能不正确
        RequestCache cache = RequestParamCacheManager.getCache(controller.getId());
        if (cache == null) {
            OpenApiWithAutoParameter.apply(openApiBuilder, project, controller);
        } else {
            OpenApiWithUserParameter.apply(openApiBuilder, project, controller);
        }
        return openApiBuilder;
    }

    private static String getBasePath(Controller controller, boolean includeHost) {
        String ipAddress = "localhost";
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

        String base = includeHost ?
                "http://" + ipAddress + ":" + controller.getServerPort() + controller.getContextPath() :
                controller.getContextPath();
        return base;
    }


    public static String toOpenApiJson(Project project, List<Controller> controllers) {
        return toOpenApiJson(project, controllers, true);
    }

    public static String toOpenApiJson(Project project, List<Controller> controllers, boolean includeHost) {
        OpenApi openApi = new OpenApi();
        for (Controller controller : controllers) {
            generatorOpenApiBuilder(project, controller, includeHost).addToOpenApi(openApi);
        }
        return GsonUtils.toJsonString(openApi);
    }

}
