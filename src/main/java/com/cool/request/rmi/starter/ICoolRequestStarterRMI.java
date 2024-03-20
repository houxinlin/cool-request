package com.cool.request.rmi.starter;

import com.cool.request.components.http.ReflexHttpRequestParamAdapterBody;
import com.cool.request.components.http.response.InvokeResponseModel;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ICoolRequestStarterRMI extends Remote {
    public InvokeResponseModel invokeController(
            ReflexHttpRequestParamAdapterBody reflexHttpRequestParamAdapterBody)
            throws RemoteException;

    public void invokeScheduled(String className, String methodName, String param) throws RemoteException;
}
