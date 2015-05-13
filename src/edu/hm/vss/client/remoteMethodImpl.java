package edu.hm.vss.client;

import edu.hm.vss.remoteMethods;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by B3rni on 13.05.2015.
 */
public class remoteMethodImpl extends UnicastRemoteObject implements remoteMethods, Serializable
{
    private static final long serialVersionUID = 1L;
    @Override
    public String sayHello() throws RemoteException
    {
        return "HUHU";
    }

    public remoteMethodImpl() throws RemoteException
    {
        super();
    }


}
