package edu.hm.vss.server;

import edu.hm.vss.interfaces.IClientToServer;
import edu.hm.vss.interfaces.IServerToClient;

/**
 * Created by B3rni on 20.05.2015.
 */
public class ClientToServer implements IClientToServer
{
    @Override
    public boolean initConnections(String ClientIP, int ClientPort, String rightNeighbourIP, int rightNeighbourPort, String leftNeighbourIP, int leftNeighbourPort)
    {
        return false;
    }

    @Override
    public boolean initServer(int seats, int maxSeats, int startIndex)
    {
        return false;
    }

    @Override
    public boolean createNewPhilosopher(int index, boolean hungry)
    {
        return false;
    }

    @Override
    public boolean respawnPhilosopher(int index, boolean hungry, int eatCount)
    {
        return false;
    }

    @Override
    public void stopServer()
    {

    }

    @Override
    public void punishPhilosopher(int index)
    {

    }

    @Override
    public boolean isReachable()
    {
        return false;
    }
}
