package com.cool.request.scan;

import com.cool.request.components.http.Controller;
import com.cool.request.components.http.StaticController;
import com.cool.request.components.http.net.HttpMethod;
import com.cool.request.utils.PsiUtils;
import com.cool.request.utils.StringUtils;
import com.intellij.openapi.module.Module;
import com.intellij.psi.PsiMethod;

import java.util.ArrayList;
import java.util.List;

public class SpringMvcControllerConverter implements ControllerConverter {
    private final SpringMvcHttpMethodParser springMvcHTTPMethodParser = new SpringMvcHttpMethodParser();
    private final SpringPathParser springPathParser = new SpringPathParser();

    @Override
    public List<StaticController> psiMethodToController(Module module, PsiMethod psiMethod, String context, int port) {
        List<StaticController> result =new ArrayList<>();
        List<HttpMethod> httpMethods = springMvcHTTPMethodParser.parserHttpMethod(psiMethod);
        List<String> httpUrl = springPathParser.parserPath(psiMethod);

        if (httpMethods.isEmpty() || httpUrl ==null) return null;

        for (String url : httpUrl) {
            StaticController controller = (StaticController) Controller.ControllerBuilder.aController()
                    .withHttpMethod(httpMethods.get(0).toString())
                    .withMethodName(psiMethod.getName())
                    .withContextPath(context)
                    .withServerPort(port)
                    .withModuleName(module.getName())
                    .withUrl(StringUtils.addPrefixIfMiss(url, "/"))
                    .withSimpleClassName(psiMethod.getContainingClass().getQualifiedName())
                    .withParamClassList(PsiUtils.getParamClassList(psiMethod))
                    .build(new StaticController(), module.getProject());
            result.add(controller);
        }
        return result;
    }

    @Override
    public PsiMethod controllerToPsiMethod(Controller controller) {
        return null;
    }
}
