package edu.hm.vss.client;

import com.sun.corba.se.spi.activation.Server;
import edu.hm.vss.helper.LogLevel;
import edu.hm.vss.helper.Logger;
import edu.hm.vss.interfaces.IServerToClient;
import edu.hm.vss.interfaces.IServerToServer;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by B3rni on 20.05.2015.
 */
public class ServerToClient extends UnicastRemoteObject implements IServerToClient
{
    private Client client;
    private boolean connectionError = false;

    private final String IPADDRESS_PATTERN = "(([0-1]?[0-9]{1,2}\\.)|(2[0-4][0-9]\\.)|(25[0-5]\\.)){3}(([0-1]?[0-9]{1,2})|(2[0-4][0-9])|(25[0-5]))";

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
        //log(ServerToClient.class.getSimpleName(), "updateEatCount - PhilIndex" + philosopherIndex + " eatCount" + eatCount);
        client.getAllEatCounts().put(philosopherIndex, eatCount);
    }

    @Override
    public void neighbourUnreachable(String IPMessage) throws RemoteException
    {
        synchronized (this)
        {
            if (!connectionError)
            {
                Pattern pattern = Pattern.compile(IPADDRESS_PATTERN);
                Matcher matcher = pattern.matcher(IPMessage);
                if (matcher.find())
                {
                    connectionError = true;
                    String IPAdress = matcher.group(0);
                    log(LogLevel.ERROR, ServerToClient.class.getSimpleName(), "neighbourUnreachable - " + IPAdress);
                    client.startFallback();

                } else
                {
                    log(LogLevel.ERROR, ServerToClient.class.getSimpleName(), "neighbourUnreachable - No IP could be found!");
                }
            }
        }
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
