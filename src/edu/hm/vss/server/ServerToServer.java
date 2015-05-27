package edu.hm.vss.server;

import edu.hm.vss.interfaces.IServerToServer;
import edu.hm.vss.model.Philosopher;

import java.rmi.RemoteException;

/**
 * Created by B3rni on 20.05.2015.
 */
public class ServerToServer implements IServerToServer
{
    @Override
    public void pushPhilosopher(int index, boolean isHungry, int eatCount, int startIndex, boolean isFirstRound) throws RemoteException
    {
        new Thread(new Philosopher(RMIServer.tablePiece, index, isHungry, eatCount, startIndex, isFirstRound)).start();
        RMIServer.clientAPI.registerPhilosopher(index, RMIServer.instanceNumber);
    }

    @Override
    public boolean requestForkToken(int index)
    {
        return false;
    }
}
