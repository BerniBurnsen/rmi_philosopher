package edu.hm.vss;

import edu.hm.vss.client.remoteMethodImpl;

import java.rmi.AlreadyBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Created by B3rni on 13.05.2015.
 */
public class RMIServer
{
    private static final int PORT = 1099;
    private static Registry registry;

    public static void startRegistry() throws RemoteException
    {
        registry = LocateRegistry.createRegistry(PORT);
    }

    public static void registerObject(String name, Remote remoteObject) throws RemoteException, AlreadyBoundException
    {
        registry.bind(name,remoteObject);
        System.out.println(name + " registered " + remoteObject.getClass().getName());
    }

    public static void main(String[] args) throws Exception
    {
        startRegistry();
        registerObject("remoteMethods", new remoteMethodImpl());
        Thread.sleep(5 * 60 * 1000);
    }
}
