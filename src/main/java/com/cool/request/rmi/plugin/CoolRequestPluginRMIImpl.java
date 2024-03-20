package com.cool.request.rmi.plugin;

import com.cool.request.components.scheduled.DynamicSpringScheduled;
import com.cool.request.components.scheduled.DynamicXxlJobScheduled;
import com.cool.request.components.ComponentType;
import com.cool.request.components.http.DynamicController;
import com.cool.request.view.tool.UserProjectManager;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class CoolRequestPluginRMIImpl extends UnicastRemoteObject implements ICoolRequestPluginRMI {
    private UserProjectManager userProjectManager;

    public CoolRequestPluginRMIImpl(UserProjectManager userProjectManager) throws RemoteException {
        this.userProjectManager = userProjectManager;
    }

    @Override
    public void loadController(List<DynamicController> dynamicController) {
        userProjectManager.addComponent(ComponentType.CONTROLLER, dynamicController);
    }

    @Override
    public void loadScheduled(List<DynamicSpringScheduled> scheduleds) throws RemoteException {
        userProjectManager.addComponent(ComponentType.SCHEDULE, scheduleds);
    }

    @Override
    public void loadXXLScheduled(List<DynamicXxlJobScheduled> xxlJobScheduleds) throws RemoteException {
        userProjectManager.addComponent(ComponentType.SCHEDULE, xxlJobScheduleds);
    }

    @Override
    public void projectStartup(int availableTcpPort, int serverPort) throws RemoteException {
        userProjectManager.addSpringBootApplicationInstance(serverPort, availableTcpPort);
    }
}
