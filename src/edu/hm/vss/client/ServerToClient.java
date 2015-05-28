package edu.hm.vss.client;

import com.sun.corba.se.spi.activation.Server;
import edu.hm.vss.helper.Logger;
import edu.hm.vss.interfaces.IServerToClient;
import edu.hm.vss.interfaces.IServerToServer;

import java.rmi.RemoteException;

/**
 * Created by B3rni on 20.05.2015.
 */
public class ServerToClient implements IServerToClient
{
    @Override
    public void updateEatCount(int philosopherIndex, int eatCount) throws RemoteException
    {
        log(ServerToClient.class.getSimpleName(), "updateEatCount - PhilIndex" + philosopherIndex + " eatCount" + eatCount);
        Client.allEatCounts.put(philosopherIndex, eatCount);
    }

    @Override
    public void neighbourUnreachable(String IP) throws RemoteException
    {
        log(ServerToClient.class.getSimpleName(),"neighbourUnreachAble - " + IP);
    }

    @Override
    public void registerPhilosopher(int index, int server) throws RemoteException
    {
        log(ServerToClient.class.getSimpleName(), "regPhil - " + index + " server: " + server);
        Client.locationMap.put(index, server);
    }

    @Override
    public int getNumberOfInstances() throws RemoteException
    {
        return Client.instanceCount;
    }

    @Override
    public void log(String type, String message) throws RemoteException
    {
        Client.logger.printLog(type, message);
    }
}
