package edu.hm.vss.model;

import edu.hm.vss.interfaces.IPlate;

import java.io.Serializable;
import java.rmi.Remote;

/**
 * Created by Joncn on 13.05.2015.
 */
public class Plate implements Serializable, Remote
{
    private Fork rightFork;
    private final int index;

    public Plate(int index)
    {
        this.index = index;
    }

    public Fork getRightFork()
    {
        return rightFork;
    }
}
