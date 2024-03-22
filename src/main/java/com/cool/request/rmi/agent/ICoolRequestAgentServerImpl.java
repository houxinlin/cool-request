package com.cool.request.rmi.agent;

import com.cool.request.agent.trace.TraceFrame;
import com.cool.request.common.constant.CoolRequestIdeaTopic;
import com.cool.request.common.state.SettingPersistentState;
import com.cool.request.common.state.SettingsState;
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
import java.util.List;

public class ICoolRequestAgentServerImpl extends UnicastRemoteObject implements ICoolRequestAgentServer {
    private Project project;
    private int maxTraceDepth;
    private boolean enabledTrace;

    public ICoolRequestAgentServerImpl(Project project) throws RemoteException {
        this.project = project;
        maxTraceDepth = SettingPersistentState.getInstance().getState().maxTraceDepth;
        enabledTrace = SettingPersistentState.getInstance().getState().enabledTrace;

        ApplicationManager.getApplication().getMessageBus()
                .connect()
                .subscribe(CoolRequestIdeaTopic.COOL_REQUEST_SETTING_CHANGE, (CoolRequestIdeaTopic.BaseListener) () -> {
                    ProgressManager.getInstance().run(new Task.Backgroundable(project, "Apply trace setting") {
                        @Override
                        public void run(@NotNull ProgressIndicator indicator) {
                            SettingsState state = SettingPersistentState.getInstance().getState();
                            if (state.maxTraceDepth != ICoolRequestAgentServerImpl.this.maxTraceDepth ||
                                    state.enabledTrace != ICoolRequestAgentServerImpl.this.enabledTrace) {
                                try {
                                    for (ICoolRequestAgentRMIInterface coolRequestAgentRMIInterface :
                                            AgentRMIManager.getAgentRMIManager(project)
                                                    .getCoolRequestAgentRMIInterfaces()) {
                                        coolRequestAgentRMIInterface.clearMethodHook();
                                    }
                                } catch (Exception ignored) {
                                }
                            }
                            ICoolRequestAgentServerImpl.this.maxTraceDepth = state.maxTraceDepth;
                            ICoolRequestAgentServerImpl.this.enabledTrace = state.enabledTrace;

                        }
                    });
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
}
