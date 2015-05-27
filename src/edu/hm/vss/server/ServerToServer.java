package edu.hm.vss.server;

import edu.hm.vss.helper.Logger;
import edu.hm.vss.interfaces.IServerToServer;
import edu.hm.vss.model.Philosopher;

import java.rmi.RemoteException;

/**
 * Created by B3rni on 20.05.2015.
 */
public class ServerToServer implements IServerToServer
{
    private final static Logger logger = Logger.getInstance();

    @Override
    public void pushPhilosopher(int index, boolean isHungry, int eatCount, int startIndex, boolean isFirstRound) throws RemoteException
    {
        logger.printLog(ServerToServer.class.getSimpleName(),"pushPhil " + index + " hungry " + isHungry + " eatCount "+ eatCount + " startIndex" + startIndex + " isFirstRound " +isFirstRound);
        new Thread(new Philosopher(RMIServer.tablePiece, index, isHungry, eatCount, startIndex, isFirstRound)).start();
        RMIServer.clientAPI.registerPhilosopher(index, RMIServer.instanceNumber);
    }

    @Override
    public boolean requestForkToken(int index)
    {
        return false;
    }
}
