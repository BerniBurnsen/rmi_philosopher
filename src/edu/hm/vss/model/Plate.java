package edu.hm.vss.model;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by Joncn on 13.05.2015.
 */
public class Plate implements Serializable, Remote
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

    public Plate()
    {
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
        //Main.writeInDebugmode(p + " obtained both" + leftFork + " " + rightFork);
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

    public void setRightFork(Fork rightFork)
    {
        this.rightFork = rightFork;
    }

    public void setIndex(int index)
    {
        this.index = index;
    }

    public void setIsReserved(boolean isReserved)
    {
        this.isReserved = isReserved;
    }

    public Philosopher getP()
    {
        return p;
    }

    public void setP(Philosopher p)
    {
        this.p = p;
    }
}
