package edu.hm.vss.model;

import edu.hm.vss.interfaces.IPhilosopher;
import edu.hm.vss.interfaces.ITable;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by B3rni on 13.05.2015.
 */
public class Philosopher extends UnicastRemoteObject implements IPhilosopher, Serializable, Runnable
{
    private static final long serialVersionUID = 1L;

    private final ITable table;
    private final int index;
    private final boolean isVeryHungry;

    private int eatCounter = 0;
    private boolean run = true;
    private boolean allowedToEat = true;
    private String state;

    public Philosopher(ITable table, int index, boolean hungry) throws RemoteException
    {
        this.table = table;
        this.index = index;
        isVeryHungry = hungry;
    }

    @Override
    public void run()
    {

    }

    @Override
    public int getIndex()
    {
        return index;
    }

    @Override
    public int getEatCounter()
    {
        return eatCounter;
    }
}
