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

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public final class ControllerMapService {
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
        UserProjectManager userProjectManager = UserProjectManager.getInstance(project);
        List<Controller> controllers = userProjectManager.getComponentByType(Controller.class);

        List<Controller> result = controllers.stream()
                .filter(controller -> controller.getOwnerPsiMethod() != null && controller.getOwnerPsiMethod().contains(targetPsiMethod))
                .collect(Collectors.toList());

        //扫描的PsiMethod和点击的PsiMethod不一样，原因不知
        if (result.isEmpty()) {
            PsiClass containingClass = targetPsiMethod.getContainingClass();
            result = controllers.stream().filter(controller -> {
                if (StringUtils.isEqualsIgnoreCase(containingClass.getQualifiedName(), controller.getJavaClassName())) {
                    if (targetPsiMethod.getName().equals(controller.getMethodName())) {
                        if (ParamUtils.isEquals(controller.getParamClassList(), PsiUtils.getParamClassList(targetPsiMethod))) {
                            return true;
                        }
                    }
                }
                return false;
            }).collect(Collectors.toList());
            if (result.size() == 1) {
                return Collections.singletonList(result.get(0));
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
