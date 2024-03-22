package com.cool.request.rmi.agent;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;

import java.util.*;

@Service
public final class AgentRMIManager {
    private List<ICoolRequestAgentRMIInterface> coolRequestAgentRMIInterfaces = new ArrayList<>();
    private Map<String, Set<String>> customMethodMap = new HashMap<>();

    public static AgentRMIManager getAgentRMIManager(Project project) {
        return project.getService(AgentRMIManager.class);

    }

    public Map<String, Set<String>> getCustomMethodMap() {
        return customMethodMap;
    }

    public void addCustomMethod(String className, String methodName) {
        customMethodMap.computeIfAbsent(className, (s) -> new HashSet<>()).add(methodName);
    }

    public List<ICoolRequestAgentRMIInterface> getCoolRequestAgentRMIInterfaces() {
        return coolRequestAgentRMIInterfaces;
    }

    public synchronized void register(ICoolRequestAgentRMIInterface coolRequestAgentRMIInterface) {
        coolRequestAgentRMIInterfaces.add(coolRequestAgentRMIInterface);

        //移除不能通信得rmi
        Iterator<ICoolRequestAgentRMIInterface> coolRequestAgentRMIInterfaceIterator = coolRequestAgentRMIInterfaces.iterator();
        while (coolRequestAgentRMIInterfaceIterator.hasNext()) {
            ICoolRequestAgentRMIInterface agentRMIInterface = coolRequestAgentRMIInterfaceIterator.next();
            try {
                agentRMIInterface.ping();
            } catch (Exception e) {
                coolRequestAgentRMIInterfaceIterator.remove();
            }
        }
    }

    public boolean hasCustomMethod(String className, String methodName) {
        return customMethodMap.computeIfAbsent(className, (s) -> new HashSet<>()).contains(methodName);
    }

    public void cancelCustomMethod(String className, String methodName) {
        customMethodMap.computeIfAbsent(className, (s) -> new HashSet<>()).remove(methodName);
        Iterator<ICoolRequestAgentRMIInterface> coolRequestAgentRMIInterfaceIterator = coolRequestAgentRMIInterfaces.iterator();
        while (coolRequestAgentRMIInterfaceIterator.hasNext()) {
            ICoolRequestAgentRMIInterface agentRMIInterface = coolRequestAgentRMIInterfaceIterator.next();
            try {
                agentRMIInterface.cancelMethodHook(className, methodName, null);
            } catch (Exception ignored) {
            }
        }
    }
}
