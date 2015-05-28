package edu.hm.vss.model;

import java.io.Serializable;

/**
 * Created by Joncn on 27.05.2015.
 */
public class ForkToken implements Serializable
{
    private int index;



    public ForkToken()
    {

    }

    public ForkToken(int index)
    {
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
