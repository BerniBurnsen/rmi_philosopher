package edu.hm.vss.server;

import edu.hm.vss.helper.Logger;
import edu.hm.vss.interfaces.IServerToServer;
import edu.hm.vss.model.ForkToken;
import edu.hm.vss.model.Philosopher;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by B3rni on 20.05.2015.
 */
public class ServerToServer extends UnicastRemoteObject implements IServerToServer
{
    private RMIServer server;

    public ServerToServer() throws RemoteException
    {
        super();

    }
    public ServerToServer(RMIServer server) throws RemoteException
    {
        super();
        this.server = server;
        System.out.println("ServerToServer contructor");
    }

    @Override
    public void pushPhilosopher(int index, boolean isHungry, int eatCount, int startIndex, boolean isFirstRound) throws RemoteException
    {
        server.getClientAPI().log(toString(), "pushPhil " + index + " hungry: " + isHungry + " eatCount: " + eatCount + " startIndex: " + startIndex + " isFirstRound: " + isFirstRound);
        new Thread(new Philosopher(server,server.getTablePiece(), index, isHungry, eatCount, startIndex, isFirstRound)).start();
        server.getClientAPI().registerPhilosopher(index, server.getInstanceNumber());
    }

    @Override
    public ForkToken requestForkToken() throws RemoteException
    {
        server.getClientAPI().log(toString(), "requestForkToken: plateSize: " + server.getPlates().size());
        return server.getPlates().get(server.getPlates().size()-1).getRightFork().getForkToken();
    }

    @Override
    public boolean requestIsRemoteForkReserved() throws RemoteException
    {
        return server.getPlates().get(server.getPlates().size()-1).getRightFork().isReserved();
    }

    @Override
    public void setIsForkReserved(boolean isReserved) throws RemoteException
    {
        server.getPlates().get(server.getPlates().size()-1).getRightFork().setIsReserved(isReserved);
    }

    @Override
    public boolean testConnection() throws RemoteException
    {
        System.out.println("SERVER " + server.getInstanceNumber());
        System.out.println("SERVER TO CLIENT " + server.getClientAPI());
        System.out.println("SERVER TO CLIENT " + server.getLeftServerAPI());
        System.out.println("SERVER TO CLIENT " + server.getRightServerAPI());
        System.out.println("SERVER TO CLIENT " + server.getPlates());
        System.out.println("SERVER " + server);
        server.getClientAPI().log(toString(), "Connection OK");
        return true;
    }

    public String toString()
    {
        return "ServerToServer " + server.getInstanceNumber();
    }

    public RMIServer getServer()
    {
        return server;
    }

    public void setServer(RMIServer server)
    {
        this.server = server;
    }
}
