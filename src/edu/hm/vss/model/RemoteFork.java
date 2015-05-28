package edu.hm.vss.model;

import edu.hm.vss.helper.Logger;
import edu.hm.vss.server.RMIServer;

import java.rmi.RemoteException;

/**
 * Created by B3rni on 20.05.2015.
 */
public class RemoteFork extends Fork
{
    private final RMIServer server;
    public RemoteFork(int index, RMIServer server)
    {
        super(index);
        this.server = server;
    }

    @Override
    public boolean isReserved()
    {
        return server.getLeftServerAPI().requestIsForkReserved(index);
    }

    @Override
    public void setIsReserved(boolean isReserved)
    {
        super.isReserved = isReserved;
        server.getLeftServerAPI().setIsForkReserved(index, isReserved);
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
    public ForkToken getForkToken()
    {
        try
        {
            server.getClientAPI().log(RemoteFork.class.getSimpleName(), "request ForkToken " + index);
            return server.getLeftServerAPI().requestForkToken(index);
        } catch (RemoteException e)
        {
            //RMIServer.clientAPI.log(RemoteFork.class.getSimpleName(), index + "Error: " + e.getMessage());
        }
        return null;
    }

    public String toString()
    {
        return "RemoteFork " + index + " " + server.getInstanceNumber();
    }
}
