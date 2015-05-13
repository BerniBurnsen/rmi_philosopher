package edu.hm.vss.model;

import edu.hm.vss.interfaces.IPhilosopher;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by B3rni on 13.05.2015.
 */
public class Philosopher extends UnicastRemoteObject implements IPhilosopher, Serializable, Runnable
{
    private static final long serialVersionUID = 1L;
    @Override
    public String sayHello() throws RemoteException
    {
        return "HUHU";
    }

    public Philosopher() throws RemoteException
    {
        super();
    }


    @Override
    public void run()
    {

    }
}
