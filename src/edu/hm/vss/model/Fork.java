package edu.hm.vss.model;

import java.io.Serializable;
import java.rmi.Remote;

/**
 * Created by Joncn on 13.05.2015.
 */
public abstract class Fork implements Serializable, Remote
{

    protected int index;
    protected boolean isReserved = false;
    protected Philosopher p = null;

    public Fork()
    {

    }

    Fork(int index)
    {
        this.index = index;
    }

    public abstract boolean isReserved();

    public abstract void setIsReserved(boolean isReserved);

    public int getIndex() { return index; }

    public abstract String toString();

    public abstract ForkToken getForkToken();

    public void setIndex(int index)
    {
        this.index = index;
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
