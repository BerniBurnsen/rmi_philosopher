package edu.hm.vss.client;

import edu.hm.vss.interfaces.IServerToClient;
import edu.hm.vss.interfaces.IServerToServer;

/**
 * Created by B3rni on 20.05.2015.
 */
public class ServerToClient implements IServerToClient
{

    @Override
    public void updateEatCount(int philosopherIndex, int eatCount)
    {
        Client.allEatCounts.put(philosopherIndex, eatCount);
    }

    @Override
    public void neighbourUnreachable(String IP)
    {

    }

    @Override
    public void registerPhilosopher(int index, int server)
    {
        Client.locationMap.put(index, server);
    }
}
