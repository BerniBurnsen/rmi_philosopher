package edu.hm.vss.model;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by Joncn on 27.05.2015.
 */
public class ForkToken extends UnicastRemoteObject implements Serializable
{
    private int index;



    public ForkToken() throws RemoteException
    {
        super();
    }

    public ForkToken(int index) throws RemoteException
    {
        super();
        this.index = index;
    }

    public int getIndex()
    {
        return index;
    }

    public void setIndex(int index)
    {
        this.index = index;
    }
}
