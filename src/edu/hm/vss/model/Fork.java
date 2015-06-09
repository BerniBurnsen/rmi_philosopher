package edu.hm.vss.model;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;


public abstract class Fork implements Serializable, Remote
{
    protected int index;
    protected Philosopher p = null;

    public Fork()
    {

    }

    Fork(int index)
    {
        this.index = index;
    }

    public int getIndex() { return index; }

    public abstract String toString();

    public void setIndex(int index)
    {
        this.index = index;
    }

//    public Philosopher getP()
//    {
//        return p;
//    }
//
//    public void setP(Philosopher p)
//    {
//        this.p = p;
//    }

    public abstract boolean tryToGet() throws RemoteException, InterruptedException;

    public abstract void waitFor() throws RemoteException, InterruptedException;

    public abstract void release() throws RemoteException;
}
