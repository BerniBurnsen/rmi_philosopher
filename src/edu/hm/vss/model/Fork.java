package edu.hm.vss.model;

import java.rmi.RemoteException;

public abstract class Fork
{
    protected int index;
    protected Philosopher p = null;

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

    public abstract boolean tryToGet() throws RemoteException, InterruptedException;

    public abstract void waitFor() throws RemoteException, InterruptedException;

    public abstract void release() throws RemoteException;
}
