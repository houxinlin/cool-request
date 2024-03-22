package com.cool.request.rmi.agent;

import com.cool.request.agent.trace.TraceFrame;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ICoolRequestAgentServer extends Remote {
    public void httpRequestFinish(List<TraceFrame> traceFrames) throws RemoteException;

    public void onStartup(int port) throws RemoteException;

    public boolean isLog() throws RemoteException;
}