package edu.hm.vss.model;

/**
 * Created by B3rni on 20.05.2015.
 */
public class LocalFork extends Fork
{

    public LocalFork(int index)
    {
        super(index);
    }
    @Override
    public boolean isReserved()
    {
        return false;
    }

    @Override
    public void setIsReserved(boolean isReserved)
    {

    }

    @Override
    public String toString()
    {
        return null;
    }
}
