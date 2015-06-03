package edu.hm.vss.model;

import java.rmi.RemoteException;

/**
 * Created by B3rni on 20.05.2015.
 */
public class LocalFork extends Fork
{
    private boolean isReserved = false;

    public LocalFork(int index)
    {
        super(index);
    }

    public Philosopher getPhilosopher()
    {
        return p;
    }

    @Override
    public String toString()
    {
        return "LocalFork: " + index;
    }

    @Override
    public synchronized boolean tryToGet() throws InterruptedException
    {
        if(isReserved)
        {
            this.wait(2);
            return false;
        }
        else
        {
            isReserved = true;
            return true;
        }
    }

    @Override
    public synchronized void waitFor() throws InterruptedException
    {
        while(isReserved)
        {
            this.wait(1);
        }
        isReserved = true;
    }

    @Override
    public synchronized void release()
    {
        isReserved = false;
        this.notify();
    }
}
