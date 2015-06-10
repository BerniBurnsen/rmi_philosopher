package edu.hm.vss.client;

import edu.hm.vss.helper.LogLevel;
import edu.hm.vss.interfaces.IServerToClient;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * implementation of the IServerToClient interface
 */
public class ServerToClient extends UnicastRemoteObject implements IServerToClient
{
    private Client client;
    private boolean connectionError = false;

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
        client.getAllEatCounts().put(philosopherIndex, eatCount);
    }

    @Override
    public void neighbourUnreachable() throws RemoteException
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                synchronized (client)
                {
                    if (!connectionError)
                    {
                        connectionError = !connectionError;
                        try
                        {
                            log(LogLevel.ERROR, ServerToClient.class.getSimpleName(), "neighbourUnreachable");
                        } catch (RemoteException e)
                        {
                            //e.printStackTrace();
                        }
                        client.startFallback();
                    }
                }
            }
        }).start();
    }

    @Override
    public void registerPhilosopher(int index, int server) throws RemoteException
    {
        log(LogLevel.CLIENT, ServerToClient.class.getSimpleName(), "regPhil - " + index + " server: " + server);
        client.getLocationMap().put(index, server);
    }

    @Override
    public int getNumberOfInstances() throws RemoteException
    {
        return client.getInstanceCount();
    }

    @Override
    public void log(LogLevel level, String type, String message) throws RemoteException
    {
        client.getLogger().printLog(level, type, message);
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
