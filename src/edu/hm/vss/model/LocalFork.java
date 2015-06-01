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
    public boolean tryToGet()
    {
        synchronized (this)
        {
            if(isReserved)
            {
                return false;
            }
            isReserved = true;
            return true;
        }
    }

    @Override
    public void waitFor()
    {
        synchronized (this)
        {
            if(isReserved)
            {
                try
                {
                    this.wait();
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
            isReserved = true;
        }
    }

    @Override
    public void release()
    {
        synchronized (this)
        {
            isReserved = false;
            this.notify();
        }
    }
}
