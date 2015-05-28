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
    private final Client client;

    public ServerToClient(Client client)
    {
        this.client = client;
        System.out.println("ServerToClient constructor");
    }

    @Override
    public void updateEatCount(int philosopherIndex, int eatCount) throws RemoteException
    {
        log(ServerToClient.class.getSimpleName(), "updateEatCount - PhilIndex" + philosopherIndex + " eatCount" + eatCount);
        client.getAllEatCounts().put(philosopherIndex, eatCount);
    }

    @Override
    public void neighbourUnreachable(String IP) throws RemoteException
    {
        log(ServerToClient.class.getSimpleName(), "neighbourUnreachAble - " + IP);
    }

    @Override
    public void registerPhilosopher(int index, int server) throws RemoteException
    {
        log(ServerToClient.class.getSimpleName(), "regPhil - " + index + " server: " + server);
        client.getLocationMap().put(index, server);
    }

    @Override
    public int getNumberOfInstances() throws RemoteException
    {
        return client.getInstanceCount();
    }

    @Override
    public void log(String type, String message) throws RemoteException
    {
        client.getLogger().printLog(type, message);
    }
}
