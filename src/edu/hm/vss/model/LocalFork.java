package edu.hm.vss.model;

/**
 * Created by B3rni on 20.05.2015.
 */
public class LocalFork extends Fork
{
    private ForkToken token;

    public LocalFork(int index)
    {
        super(index);
        token = new ForkToken(index);
    }
    @Override
    public boolean isReserved()
    {
        return isReserved;
    }

    @Override
    public void setIsReserved(boolean isReserved)
    {
        super.isReserved = isReserved;
        if(isReserved)
        {
            this.p = p;
        }
        else
        {
            this.p = null;
        }
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
    public ForkToken getForkToken()
    {
        return token;
    }
}
