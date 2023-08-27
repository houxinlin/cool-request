//package com.hxl.plugin.springboot.invoke.utils;
//
//import com.hxl.plugin.springboot.invoke.springmvc.RequestCache;
//import com.hxl.plugin.springboot.invoke.model.RequestMappingModel;
//import com.hxl.plugin.springboot.invoke.springmvc.param.*;
//import com.intellij.openapi.project.Project;
//import com.intellij.openapi.project.ProjectManager;
//import com.intellij.psi.PsiClass;
//import com.intellij.psi.PsiMethod;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class ParameterUtils {
//    private static final List<RequestParamSpeculate> requestParamSpeculates = new ArrayList<>();
//
//    static {
//        requestParamSpeculates.add(new UrlParamSpeculate());
//        requestParamSpeculates.add(new HeaderParamSpeculate());
//        requestParamSpeculates.add(new JsonBodyParamSpeculate());
//        requestParamSpeculates.add(new FormDataSpeculate());
//        requestParamSpeculates.add(new UrlencodedSpeculate());
//    }
//
//    public static RequestCache createDefaultRequestCache(RequestMappingModel requestMappingModel) {
//        Project openProject = ProjectManager.getInstance().getOpenProjects()[0];
//        PsiClass psiClass = PsiUtils.findClassByName(openProject, requestMappingModel.getController().getSimpleClassName());
//        if (psiClass != null) {
//            PsiMethod methodInClass = PsiUtils.findMethodInClass(psiClass, requestMappingModel.getController().getMethodName());
//            RequestCache.RequestCacheBuilder requestCacheBuilder = RequestCache.RequestCacheBuilder.aRequestCache()
//                    .withInvokeModelIndex(1);
////            for (RequestParamSpeculate requestParamSpeculate : requestParamSpeculates) {
////                requestParamSpeculate.set(methodInClass,  requestCacheBuilder);
////            }
//            return requestCacheBuilder.build();
//        }
//        return null;
//    }
//
//}
