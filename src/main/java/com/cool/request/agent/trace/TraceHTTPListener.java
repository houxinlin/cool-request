package com.cool.request.agent.trace;

import com.cool.request.common.state.SettingPersistentState;
import com.cool.request.components.http.CustomController;
import com.cool.request.components.http.net.RequestContext;
import com.cool.request.rmi.agent.AgentRMIManager;
import com.cool.request.rmi.agent.ICoolRequestAgentRMIInterface;
import com.cool.request.view.main.HTTPEventListener;
import com.cool.request.view.page.TracePreviewView;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TraceHTTPListener implements HTTPEventListener {
    private TracePreviewView tracePreviewView;
    private Project project;


    public TraceHTTPListener(Project project, TracePreviewView tracePreviewView) {
        this.tracePreviewView = tracePreviewView;
        this.project = project;
    }

    @Override
    public void beginSend(RequestContext requestContext, ProgressIndicator progressIndicator) {
        tracePreviewView.setTraceFrame(new ArrayList<>());
        if (requestContext.getController() instanceof CustomController) return;
        if (!SettingPersistentState.getInstance().getState().enabledTrace) return;
        Map<String, Set<String>> customMethodMap = AgentRMIManager.getAgentRMIManager(project).getCustomMethodMap();
        boolean userByteCodeCache = SettingPersistentState.getInstance().getState().userByteCodeCache;
        boolean useTraceLog = SettingPersistentState.getInstance().getState().useTraceLog;
        progressIndicator.setText("Trace init...");
        Set<String> alreadyAdd = new HashSet<>();
        for (ICoolRequestAgentRMIInterface coolRequestAgentRMIInterface :
                AgentRMIManager.getAgentRMIManager(project).getCoolRequestAgentRMIInterfaces()) {
            try {
                if (!coolRequestAgentRMIInterface.ping()) return;
                coolRequestAgentRMIInterface.addMethodHook(
                        requestContext.getController().getSimpleClassName(),
                        requestContext.getController().getMethodName(),
                        SettingPersistentState.getInstance().getState().maxTraceDepth,
                        true,
                        userByteCodeCache,
                        useTraceLog);

                alreadyAdd.add(requestContext.getController().getSimpleClassName() + "." + requestContext.getController().getMethodName());
                for (String className : customMethodMap.keySet()) {
                    for (String methodName : customMethodMap.getOrDefault(className, new HashSet<>())) {
                        if (alreadyAdd.contains(className + "." + methodName)) continue;
                        coolRequestAgentRMIInterface.addMethodHook(className,
                                methodName,
                                1,
                                false,
                                userByteCodeCache,
                                useTraceLog);
                    }
                }
            } catch (Exception e) {

            }
        }
    }
}
