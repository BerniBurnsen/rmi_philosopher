package edu.hm.vss.model;

import edu.hm.vss.helper.Logger;
import edu.hm.vss.server.RMIServer;

import java.rmi.RemoteException;

/**
 * Created by B3rni on 20.05.2015.
 */
public class RemoteFork extends Fork
{
    private static final Logger logger = Logger.getInstance();

    public RemoteFork(int index)
    {
        super(index);
    }

    @Override
    public boolean isReserved()
    {
        return RMIServer.rightServerAPI.requestIsForkReserved(index);
    }

    @Override
    public void setIsReserved(boolean isReserved)
    {
        super.isReserved = isReserved;
        RMIServer.rightServerAPI.setIsForkReserved(index, isReserved);
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
        try
        {
            logger.printLog(RemoteFork.class.getSimpleName(), "request ForkToken " + index);
            return RMIServer.leftServerAPI.requestForkToken(index);
        } catch (RemoteException e)
        {
            logger.printLog(RemoteFork.class.getSimpleName(), index + "Error: " + e.getMessage());
        }
        return null;
    }
}
