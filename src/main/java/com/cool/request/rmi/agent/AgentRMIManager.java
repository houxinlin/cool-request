/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * AgentRMIManager.java is part of Cool Request
 *
 * License: GPL-3.0+
 *
 * Cool Request is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Cool Request is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Cool Request.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.cool.request.rmi.agent;

import com.cool.request.common.state.SettingPersistentState;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;

import java.util.*;

@Service
public final class AgentRMIManager {
    private final List<ICoolRequestAgentRMIInterface> coolRequestAgentRMIInterfaces = new ArrayList<>();

    public static AgentRMIManager getAgentRMIManager(Project project) {
        return project.getService(AgentRMIManager.class);
    }

    public Map<String, Set<String>> getCustomMethodMap() {
        return SettingPersistentState.getInstance().getState().traceMap;
    }

    public void clear() {
        getCustomMethodMap().forEach((clz, methods) -> methods.forEach(method -> {
            for (ICoolRequestAgentRMIInterface agentRMIInterface : coolRequestAgentRMIInterfaces) {
                try {
                    agentRMIInterface.cancelMethodHook(clz, method, null);
                } catch (Exception ignored) {
                }
            }
        }));
        SettingPersistentState.getInstance().getState().traceMap = new HashMap<>();
    }

    public void addCustomMethod(String className, String methodName) {
        SettingPersistentState.getInstance()
                .getState()
                .traceMap
                .computeIfAbsent(className, (s) -> new HashSet<>()).add(methodName);
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
        return SettingPersistentState.getInstance().getState().traceMap.computeIfAbsent(className, (s) -> new HashSet<>()).contains(methodName);
    }

    public void cancelCustomMethod(String className, String methodName) {
        SettingPersistentState.getInstance().getState().traceMap.computeIfAbsent(className, (s) -> new HashSet<>()).remove(methodName);
        for (ICoolRequestAgentRMIInterface agentRMIInterface : coolRequestAgentRMIInterfaces) {
            try {
                agentRMIInterface.cancelMethodHook(className, methodName, null);
            } catch (Exception ignored) {
            }
        }
    }
}
