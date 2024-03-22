package com.cool.request.rmi.agent;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ICoolRequestAgentRMIInterface extends Remote {
    public boolean addMethodHook(String className,
                                 String methodName,
                                 int maxDepth,
                                 boolean entryPoint,
                                 boolean useCache,
                                 boolean useLog) throws RemoteException;

    public boolean clearMethodHook() throws RemoteException;

    public boolean ping() throws RemoteException;

    public boolean cancelMethodHook(String className, String methodName, String methodDesc) throws RemoteException;
}
