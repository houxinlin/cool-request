package com.cool.request.common.service;

import com.cool.request.common.bean.components.controller.Controller;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiMethod;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
public final class ControllerMapService {
    private final Map<Controller, PsiMethod> apiMethodMap = new HashMap<>();

    public static final ControllerMapService getInstance(Project project) {
        return project.getService(ControllerMapService.class);
    }

    public Collection<PsiMethod> values() {
        return apiMethodMap.values();
    }

    public Controller findUrl(PsiMethod psiMethod) {
        for (Controller s : apiMethodMap.keySet()) {
            if (apiMethodMap.get(s) == psiMethod) return s;
        }
        return null;

    }

    public void addMap(Controller url, PsiMethod psiMethod) {
        apiMethodMap.put(url, psiMethod);
    }

    public void clear() {
        apiMethodMap.clear();
    }
}
