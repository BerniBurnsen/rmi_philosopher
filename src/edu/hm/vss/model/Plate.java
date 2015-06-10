package edu.hm.vss.model;

import java.rmi.RemoteException;

/**
 * represents a place with plate on the tablepiece
 */
public class Plate
{
    private Fork leftFork;
    private Fork rightFork;

    private int index;

    private boolean isReserved = false;
    private Philosopher p = null;

    public Plate(Fork leftFork, Fork rightFork, int index)
    {
        this.leftFork = leftFork;
        this.rightFork = rightFork;
        this.index = index;
    }

    public boolean isReserved()
    {
        return isReserved;
    }

    public void setIsReserved(boolean isReserved, Philosopher p)
    {
        this.isReserved = isReserved;
        if(isReserved)
        {
            this.p = p;
        }
        else
        {
            this.p = null;
        }
    }

    public int getIndex()
    {
        return index;
    }

    public void waitForForks(Philosopher p) throws InterruptedException, RemoteException
    {
        Fork firstFork;
        Fork secondFork;
        if(this.getIndex() %2 == 0)
        {
            firstFork = leftFork;
            secondFork = rightFork;
        }
        else
        {
            firstFork = rightFork;
            secondFork = leftFork;
        }
        boolean obtained = false;
        while(!obtained)
        {
            firstFork.waitFor();
            if(secondFork.tryToGet());
            {
                obtained = true;
            }
            firstFork.release();
        }
    }

    public void releaseForks() throws RemoteException
    {
        leftFork.release();
        rightFork.release();
    }

    public Fork getLeftFork()
    {
        return leftFork;
    }

    public Fork getRightFork()
    {
        return rightFork;
    }

    public Philosopher getPhilosopher()
    {
        return p;
    }

    public String toString()
    {
        return "Plate " + getIndex();
    }

    public void setLeftFork(Fork leftFork)
    {
        this.leftFork = leftFork;
    }

    public void setIndex(int index)
    {
        this.index = index;
    }
}