package com.cool.request.common.service;

import com.cool.request.common.bean.components.controller.Controller;
import com.cool.request.view.main.MainTopTreeView;
import com.cool.request.view.main.MainTopTreeViewManager;
import com.cool.request.view.tool.ProviderManager;
import com.cool.request.view.tool.UserProjectManager;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public final class ControllerMapService {

    //    private final Map<Controller, List<PsiMethod>> apiMethodMap = new HashMap<>();
//
    public static final ControllerMapService getInstance(Project project) {
        return project.getService(ControllerMapService.class);
    }

    public List<Controller> findControllerByPsiMethod(Project project, PsiMethod targetPsiMethod) {
        return ProviderManager.findAndConsumerProvider(
                UserProjectManager.class,
                project,
                userProjectManager -> {
                    List<Controller> controllers = userProjectManager.getComponentByType(Controller.class);
                    return controllers.stream()
                            .filter(controller -> controller.getOwnerPsiMethod().contains(targetPsiMethod))
                            .collect(Collectors.toList());
                }, new ArrayList<>());
    }

    public MainTopTreeView.RequestMappingNode findRequestMappingNodeByController(Project project, Controller controller) {
        return ProviderManager.findAndConsumerProvider(MainTopTreeViewManager.class, project, mainTopTreeViewManager -> {
            Map<MainTopTreeView.TreeNode<?>, List<MainTopTreeView.RequestMappingNode>> requestMappingNodeMap = mainTopTreeViewManager.getRequestMappingNodeMap();
            for (List<MainTopTreeView.RequestMappingNode> requestMappingNodes : requestMappingNodeMap.values()) {
                for (MainTopTreeView.RequestMappingNode requestMappingNode : requestMappingNodes) {
                    if (controller == requestMappingNode.getData()) return requestMappingNode;
                }
            }
            return null;
        });
    }
}
