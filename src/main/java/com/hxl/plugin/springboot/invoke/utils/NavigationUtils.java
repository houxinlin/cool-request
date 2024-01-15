package com.hxl.plugin.springboot.invoke.utils;

import com.hxl.plugin.springboot.invoke.Constant;
import com.hxl.plugin.springboot.invoke.IdeaTopic;
import com.hxl.plugin.springboot.invoke.bean.components.controller.Controller;
import com.hxl.plugin.springboot.invoke.net.HttpMethod;
import com.hxl.plugin.springboot.invoke.springmvc.utils.ParamUtils;
import com.hxl.plugin.springboot.invoke.view.CoolIdeaPluginWindowView;
import com.hxl.plugin.springboot.invoke.view.main.MainTopTreeView;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;

import java.util.List;

public class NavigationUtils {


    /**
     * This method queries the scheduled tasks for the clicked method.
     *
     * @param project                  The current project.
     * @param clickedMethod            The clicked method.
     * @param qualifiedName            The qualified name of the clicked method.
     * @return True if the scheduled task was found, false otherwise.
     */
    public static boolean navigationScheduledInMainJTree(Project project,
                                                         PsiMethod clickedMethod,
                                                         String qualifiedName) {
        CoolIdeaPluginWindowView coolIdeaPluginWindowView = project.getUserData(Constant.CoolIdeaPluginWindowViewKey);
        if (coolIdeaPluginWindowView == null) return false;

        for (List<MainTopTreeView.ScheduledMethodNode> value : coolIdeaPluginWindowView.getMainTopTreeView().getScheduleMapNodeMap().values()) {
            for (MainTopTreeView.ScheduledMethodNode scheduledMethodNode : value) {
                if (scheduledMethodNode.getData().getClassName().equals(qualifiedName) &&
                        clickedMethod.getName().equals(scheduledMethodNode.getData().getMethodName())) {
                    project.getMessageBus()
                            .syncPublisher(IdeaTopic.SCHEDULED_CHOOSE_EVENT)
                            .onChooseEvent(scheduledMethodNode.getData());
                    coolIdeaPluginWindowView.getMainTopTreeView().selectNode(scheduledMethodNode);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * This method queries the controller node for the clicked method.
     *
     * @param project   The current project.
     * @param psiMethod The clicked method.
     * @return True if the controller node was found, false otherwise.
     */
    public static boolean navigationControllerInMainJTree(Project project, PsiMethod psiMethod) {
        CoolIdeaPluginWindowView coolIdeaPluginWindowView = project.getUserData(Constant.CoolIdeaPluginWindowViewKey);
        if (coolIdeaPluginWindowView == null) return false;

        List<HttpMethod> supportMethod = PsiUtils.getHttpMethod(psiMethod);
        if (supportMethod.isEmpty()) {
            return false;
        }
        List<String> httpUrl = ParamUtils.getHttpUrl(psiMethod);
        if (httpUrl == null) {
            return false;
        }
        String methodClassName = "";
        PsiClass containingClass = psiMethod.getContainingClass();
        if (containingClass != null) {
            methodClassName = psiMethod.getContainingClass().getQualifiedName();
        }

        String methodName = psiMethod.getName();
        MainTopTreeView.RequestMappingNode result = null;
        int max = -1;

        for (List<MainTopTreeView.RequestMappingNode> value : coolIdeaPluginWindowView.getMainTopTreeView().getRequestMappingNodeMap().values()) {
            for (MainTopTreeView.RequestMappingNode requestMappingNode : value) {
                Controller controller = requestMappingNode.getData();

                if (controller.getSimpleClassName().equals(methodClassName) &&
                        ParamUtils.httpMethodIn(supportMethod, HttpMethod.parse(controller.getHttpMethod()))) {

                    if (methodName.equals(controller.getMethodName()) &&
                            ParamUtils.isEquals(controller.getParamClassList(), PsiUtils.getParamClassList(psiMethod))) {
                        project.getMessageBus()
                                .syncPublisher(IdeaTopic.CONTROLLER_CHOOSE_EVENT)
                                .onChooseEvent(requestMappingNode.getData());
                        coolIdeaPluginWindowView.getMainTopTreeView().selectNode(requestMappingNode);
                        return true;
                    } else {
                        for (String urlItem : httpUrl) {
                            if (controller.getUrl().endsWith(urlItem) &&
                                    urlItem.length() > max && ParamUtils.httpMethodIn(supportMethod, HttpMethod.parse(controller.getHttpMethod()))) {
                                max = urlItem.length();
                                result = requestMappingNode;
                            }
                        }
                    }
                }
            }
        }
        if (result != null) {
            project.getMessageBus()
                    .syncPublisher(IdeaTopic.CONTROLLER_CHOOSE_EVENT)
                    .onChooseEvent(result.getData());
            coolIdeaPluginWindowView.getMainTopTreeView().selectNode(result);
            return true;
        }
        return false;
    }


}
