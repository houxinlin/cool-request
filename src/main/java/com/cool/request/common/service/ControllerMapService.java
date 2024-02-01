package com.cool.request.common.service;

import com.cool.request.common.bean.components.controller.Controller;
import com.cool.request.view.main.MainTopTreeView;
import com.cool.request.view.tool.ProviderManager;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public final class ControllerMapService {

    //    private final Map<Controller, List<PsiMethod>> apiMethodMap = new HashMap<>();
//
    public static final ControllerMapService getInstance(Project project) {
        return project.getService(ControllerMapService.class);
    }

    public List<Controller> findControllerByPsiMethod(Project project, PsiMethod targetPsiMethod) {
        MainTopTreeView mainTopTreeView = ProviderManager.getProvider(MainTopTreeView.class, project);
        if (mainTopTreeView == null) return new ArrayList<>();
        List<Controller> result = new ArrayList<>();
        Map<MainTopTreeView.ClassNameNode, List<MainTopTreeView.RequestMappingNode>> requestMappingNodeMap = mainTopTreeView.getRequestMappingNodeMap();
        for (List<MainTopTreeView.RequestMappingNode> requestMappingNodes : requestMappingNodeMap.values()) {
            for (MainTopTreeView.RequestMappingNode requestMappingNode : requestMappingNodes) {
                for (PsiMethod psiMethod : requestMappingNode.getData().getOwnerPsiMethod()) {
                    if (psiMethod == targetPsiMethod) result.add(requestMappingNode.getData());
                }
            }
        }
        return result;
    }

    public MainTopTreeView.RequestMappingNode findRequestMappingNodeByController(Project project, Controller controller) {
        MainTopTreeView mainTopTreeView = ProviderManager.getProvider(MainTopTreeView.class, project);
        if (mainTopTreeView == null) return null;
        Map<MainTopTreeView.ClassNameNode, List<MainTopTreeView.RequestMappingNode>> requestMappingNodeMap = mainTopTreeView.getRequestMappingNodeMap();
        for (List<MainTopTreeView.RequestMappingNode> requestMappingNodes : requestMappingNodeMap.values()) {
            for (MainTopTreeView.RequestMappingNode requestMappingNode : requestMappingNodes) {
                if (controller == requestMappingNode.getData()) return requestMappingNode;
            }
        }
        return null;
    }
}
