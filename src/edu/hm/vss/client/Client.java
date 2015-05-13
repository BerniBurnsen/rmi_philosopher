package edu.hm.vss.client;

import edu.hm.vss.remoteMethods;

import java.math.BigDecimal;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Created by B3rni on 13.05.2015.
 */
public class Client
{
    private static final String HOST = "localhost";
    private static final int PORT = 1099;

    private static Registry registry;
    public static void main(String args[]) throws RemoteException, NotBoundException
    {
        registry = LocateRegistry.getRegistry(HOST,PORT);
        remoteMethods remoteApi = (remoteMethods)registry.lookup("remoteMethods");
        System.out.println(remoteApi.sayHello());

    }
}
