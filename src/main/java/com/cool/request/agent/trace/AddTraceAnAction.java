package com.cool.request.agent.trace;

import com.cool.request.common.icons.CoolRequestIcons;
import com.cool.request.rmi.agent.AgentRMIManager;
import com.cool.request.utils.NavigationUtils;
import com.cool.request.utils.ResourceBundleUtils;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;

public class AddTraceAnAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        assert project != null;
        PsiMethod clickedMethod = NavigationUtils.findClickedMethod(e);
        if (clickedMethod == null) {
            return;
        }
        String className = clickedMethod.getContainingClass().getQualifiedName();
        String methodName = clickedMethod.getName();

        if (AgentRMIManager.getAgentRMIManager(project).hasCustomMethod(className, methodName)) {
            String msg = ResourceBundleUtils.getString("cancel.method.to.trace") + "\n\n" + className + "." + methodName;
            if (Messages.showOkCancelDialog(msg, ResourceBundleUtils.getString("tip"), CoolRequestIcons.MAIN) == 0) {
                AgentRMIManager.getAgentRMIManager(project).cancelCustomMethod(className, methodName);
            }
            return;
        }
        String msg = ResourceBundleUtils.getString("add.method.to.trace") + "\n\n" + className + "." + methodName;
        if (Messages.showOkCancelDialog(msg, ResourceBundleUtils.getString("tip"), CoolRequestIcons.MAIN) == 0) {
            AgentRMIManager.getAgentRMIManager(project).addCustomMethod(className, methodName);
        }
    }

}
