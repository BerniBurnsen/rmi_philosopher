package edu.hm.vss.server;

import edu.hm.vss.helper.Logger;
import edu.hm.vss.interfaces.IServerToServer;
import edu.hm.vss.model.ForkToken;
import edu.hm.vss.model.Philosopher;

import java.rmi.RemoteException;

/**
 * Created by B3rni on 20.05.2015.
 */
public class ServerToServer implements IServerToServer
{
    private final RMIServer server;
    public ServerToServer(RMIServer server)
    {
        this.server =server;
    }

    @Override
    public void pushPhilosopher(int index, boolean isHungry, int eatCount, int startIndex, boolean isFirstRound) throws RemoteException
    {
        server.getClientAPI().log(ServerToServer.class.getSimpleName() + server.getInstanceNumber(), "pushPhil " + index + " hungry: " + isHungry + " eatCount: " + eatCount + " startIndex: " + startIndex + " isFirstRound: " + isFirstRound);
        new Thread(new Philosopher(server,server.getTablePiece(), index, isHungry, eatCount, startIndex, isFirstRound)).start();
        server.getClientAPI().registerPhilosopher(index, server.getInstanceNumber());
    }

    @Override
    public ForkToken requestForkToken(int index)
    {
        return server.getPlates().get(index).getRightFork().getForkToken();
    }

    @Override
    public boolean requestIsForkReserved(int index)
    {
        return server.getPlates().get(index).getRightFork().isReserved();
    }

    @Override
    public void setIsForkReserved(int index, boolean isReserved)
    {
        server.getPlates().get(index).getRightFork().setIsReserved(isReserved);
    }
}
