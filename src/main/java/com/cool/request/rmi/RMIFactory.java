package com.cool.request.rmi;

import com.cool.request.rmi.starter.ICoolRequestStarterRMI;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public class RMIFactory {
    public static ICoolRequestStarterRMI getStarterRMI(int port) {
        return (ICoolRequestStarterRMI) getRMIInstance(port, ICoolRequestStarterRMI.class.getName());
    }

    public static Remote getRMIInstance(int port, String name) {
        try {
            return Naming.lookup("rmi://localhost:" + port + "/" + name);
        } catch (NotBoundException e) {
            throw new RuntimeException(e);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
