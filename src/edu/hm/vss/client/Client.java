package edu.hm.vss.client;

import edu.hm.vss.interfaces.ITest;
import edu.hm.vss.interfaces.Settings;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Created by B3rni on 13.05.2015.
 */
public class Client
{
    private static Registry registry;
    public static void main(String args[]) throws RemoteException, NotBoundException
    {
        //System.out.println(new Test().doSomethingExpensive());
        registry = LocateRegistry.getRegistry(Settings.SERVER, Settings.PORT);
        ITest remoteApi = (ITest)registry.lookup("Test");
        System.out.println(remoteApi.doSomethingExpensive());
    }
}
