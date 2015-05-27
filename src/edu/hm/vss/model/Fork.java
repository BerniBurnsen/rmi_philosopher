package edu.hm.vss.model;

import java.io.Serializable;
import java.rmi.Remote;

/**
 * Created by Joncn on 13.05.2015.
 */
public abstract class Fork implements Serializable, Remote
{
    private final int index;
    private boolean isReserved = false;
    private Philosopher p = null;

    Fork(int index)
    {
        this.index = index;
    }

    public abstract boolean isReserved();

    public abstract void setIsReserved(boolean isReserved);

    public int getIndex() { return index; }

    public abstract String toString();
}
