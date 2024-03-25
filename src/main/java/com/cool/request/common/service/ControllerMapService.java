package com.cool.request.common.service;

import com.cool.request.components.http.Controller;
import com.cool.request.lib.springmvc.utils.ParamUtils;
import com.cool.request.utils.PsiUtils;
import com.cool.request.utils.StringUtils;
import com.cool.request.view.main.MainTopTreeView;
import com.cool.request.view.main.MainTopTreeViewManager;
import com.cool.request.view.tool.ProviderManager;
import com.cool.request.view.tool.UserProjectManager;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;

import java.util.ArrayList;
import java.util.Arrays;
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

    /**
     * 从UserProjectManager找到和PsiMethod对应的Controller
     * <p>
     * 这里这样做的原因是需要进行Tree导航，直接查询在HTTP界面中显示无法导航
     * <p>
     * 静态扫描出的数据，比如有${}可能不准确，动态补全后就有多个Controller实例
     *
     * @param project
     * @param targetPsiMethod
     * @return
     */
    public List<Controller> findControllerByPsiMethod(Project project, PsiMethod targetPsiMethod) {
        //todo 优化
        if (ProviderManager.getProvider(UserProjectManager.class, project) == null) return new ArrayList<>();
        List<Controller> result = ProviderManager.findAndConsumerProvider(
                UserProjectManager.class,
                project,
                userProjectManager -> {
                    List<Controller> controllers = userProjectManager.getComponentByType(Controller.class);
                    return controllers.stream()
                            .filter(controller -> controller.getOwnerPsiMethod() != null && controller.getOwnerPsiMethod().contains(targetPsiMethod))
                            .collect(Collectors.toList());
                }, new ArrayList<>());
        //扫描的PsiMethod和点击的PsiMethod不一样，原因不知
        if (result.isEmpty()) {
            result = ProviderManager.findAndConsumerProvider(
                    UserProjectManager.class,
                    project,
                    userProjectManager -> {
                        List<Controller> controllers = userProjectManager.getComponentByType(Controller.class);
                        PsiClass containingClass = targetPsiMethod.getContainingClass();
                        return controllers.stream().filter(controller -> {
                            if (StringUtils.isEqualsIgnoreCase(containingClass.getQualifiedName(), controller.getJavaClassName())) {
                                if (targetPsiMethod.getName().equals(controller.getMethodName())) {
                                    if (ParamUtils.isEquals(controller.getParamClassList(), PsiUtils.getParamClassList(targetPsiMethod))) {
                                        return true;
                                    }
                                }
                            }
                            return false;
                        }).collect(Collectors.toList());
                    }, new ArrayList<>());
            if (result.size() == 1) {
                return Arrays.asList(result.get(0));
            }
        }
        return result;
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
