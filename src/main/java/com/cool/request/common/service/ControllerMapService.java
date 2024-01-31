package com.cool.request.common.service;

import com.cool.request.common.bean.components.controller.Controller;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiMethod;

import java.util.*;
import java.util.function.Function;

@Service
public final class ControllerMapService {
    /**
     * 每个Controller对应一个或者多个相关方法，这里可能是接口里面的
     */
//    private final Map<Controller, List<PsiMethod>> apiMethodMap = new HashMap<>();
//
//    public static final ControllerMapService getInstance(Project project) {
//        return project.getService(ControllerMapService.class);
//    }
//
//    public Collection<List<PsiMethod>> values() {
//        return apiMethodMap.values();
//    }
//
//    public Controller findUrl(PsiMethod psiMethod) {
//        for (Controller s : apiMethodMap.keySet()) {
//            List<PsiMethod> psiMethods = apiMethodMap.getOrDefault(s,new ArrayList<>());
//            for (PsiMethod method : psiMethods) {
//                if (method ==psiMethod)return s;
//            }
//        }
//        return null;
//    }
//
//    public void addMap(Controller url, PsiMethod psiMethod) {
//        apiMethodMap.computeIfAbsent(url, controller -> new ArrayList<>()).add(psiMethod);
//    }

//    public void clear() {
//        apiMethodMap.clear();
//    }
}
