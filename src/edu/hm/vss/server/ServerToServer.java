package edu.hm.vss.server;

import edu.hm.vss.interfaces.IServerToServer;

/**
 * Created by B3rni on 20.05.2015.
 */
public class ServerToServer implements IServerToServer
{
    @Override
    public void pushPhilosopher(int index, boolean isHungry, int eatCount)
    {

    }

    @Override
    public boolean requestForkToken(int index)
    {
        return false;
    }
}
