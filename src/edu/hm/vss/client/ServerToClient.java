package edu.hm.vss.client;

import com.sun.corba.se.spi.activation.Server;
import edu.hm.vss.helper.Logger;
import edu.hm.vss.interfaces.IServerToClient;
import edu.hm.vss.interfaces.IServerToServer;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by B3rni on 20.05.2015.
 */
public class ServerToClient extends UnicastRemoteObject implements IServerToClient
{
    private Client client;

    public ServerToClient() throws RemoteException
    {
        super();
    }

    public ServerToClient(Client client) throws RemoteException
    {
        super();
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

    public Client getClient()
    {
        return client;
    }

    public void setClient(Client client)
    {
        this.client = client;
    }
}
