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

    public Philosopher getPhilosopher()
    {
        return p;
    }

    @Override
    public boolean tryToGet() throws RemoteException, InterruptedException
    {
        return server.getLeftServerAPI().tryToGetFork();
    }

    @Override
    public void waitFor() throws RemoteException, InterruptedException
    {
        server.getLeftServerAPI().waitForFork();
    }

    @Override
    public void release() throws RemoteException
    {
        server.getLeftServerAPI().releaseFork();
    }

    public String toString()
    {
        return "RemoteFork " + index + " " + server.getInstanceNumber();
    }
}
