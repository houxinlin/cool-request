/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * ICoolRequestAgentServerImpl.java is part of Cool Request
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

import com.cool.request.agent.trace.TraceFrame;
import com.cool.request.common.constant.CoolRequestIdeaTopic;
import com.cool.request.common.state.SettingPersistentState;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class ICoolRequestAgentServerImpl extends UnicastRemoteObject implements ICoolRequestAgentServer, CoolRequestIdeaTopic.BaseListener {
    private Project project;
    private List<FlagListener> flagListeners = new ArrayList<>();

    public ICoolRequestAgentServerImpl(Project project) throws RemoteException {
        this.project = project;

        flagListeners.add(new FlagListener(() -> SettingPersistentState.getInstance().getState().maxTraceDepth));
        flagListeners.add(new FlagListener(() -> SettingPersistentState.getInstance().getState().enabledTrace));
        flagListeners.add(new FlagListener(() -> SettingPersistentState.getInstance().getState().traceMybatis));
        ApplicationManager.getApplication().getMessageBus()
                .connect()
                .subscribe(CoolRequestIdeaTopic.COOL_REQUEST_SETTING_CHANGE, this);
    }

    @Override
    public void event() {
        ProgressManager.getInstance().run(new Task.Backgroundable(project, "Trace setting checking... ") {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                indicator.setFraction(0.8);
                for (FlagListener flagListener : flagListeners) {
                    if (flagListener.isChange()) {
                        indicator.setText("Trace apply...");
                        try {
                            for (ICoolRequestAgentRMIInterface coolRequestAgentRMIInterface :
                                    AgentRMIManager.getAgentRMIManager(project)
                                            .getCoolRequestAgentRMIInterfaces()) {
                                coolRequestAgentRMIInterface.clearMethodHook();
                            }
                        } catch (Exception ignored) {
                        }
                        break;
                    }
                }
                for (FlagListener flagListener : flagListeners) {
                    flagListener.reset();
                }

            }
        });
    }

    @Override
    public void httpRequestFinish(List<TraceFrame> traceFrames) throws RemoteException {
        project.getMessageBus().syncPublisher(CoolRequestIdeaTopic.TRACE_FINISH).traceFinish(traceFrames);
    }

    @Override
    public boolean isLog() throws RemoteException {
        return SettingPersistentState.getInstance().getState().useTraceLog;
    }

    @Override
    public void onStartup(int port) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", port);
            Remote lookup = registry.lookup(ICoolRequestAgentRMIInterface.class.getName());
            AgentRMIManager.getAgentRMIManager(project).register((ICoolRequestAgentRMIInterface) lookup);
        } catch (RemoteException e) {
        } catch (NotBoundException e) {
        }
    }

    class FlagListener<T> {
        private T value;
        private Supplier<T> valueSupplier;

        public FlagListener(Supplier<T> function) {
            this.valueSupplier = function;
            this.value = function.get();
        }

        public boolean isChange() {
            if (Objects.equals(valueSupplier.get(), value)) return true;
            return false;
        }

        public void reset() {
            this.value = valueSupplier.get();
        }
    }

}
