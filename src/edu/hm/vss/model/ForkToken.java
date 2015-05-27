package edu.hm.vss.model;

import java.io.Serializable;

/**
 * Created by Joncn on 27.05.2015.
 */
public class ForkToken implements Serializable
{
    private int index;

    public ForkToken(int index)
    {
        this.index = index;
    }
}
