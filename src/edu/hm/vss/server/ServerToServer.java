package edu.hm.vss.server;

import edu.hm.vss.interfaces.IServerToServer;
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
    public boolean pushPhilosopher(int index, boolean isHungry, int eatCount, int startIndex, boolean isFirstRound) throws RemoteException
    {
        if(server.isRun())
        {
            server.getClientAPI().log(toString(), "pushPhil " + index + " hungry: " + isHungry + " eatCount: " + eatCount + " startIndex: " + startIndex + " isFirstRound: " + isFirstRound);
            Philosopher p = new Philosopher(server, server.getTablePiece(), index, isHungry, eatCount, startIndex, isFirstRound);
            server.getPhilosophers().put(index, p);
            p.start();
            server.getClientAPI().registerPhilosopher(index, server.getInstanceNumber());
            return true;
        }
        return false;
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

    @Override
    public boolean tryToGetFork() throws RemoteException
    {
        return server.getPlates().get(server.getPlates().size()-1).getRightFork().tryToGet();
    }

    @Override
    public void waitForFork() throws RemoteException
    {
        server.getPlates().get(server.getPlates().size()-1).getRightFork().waitFor();
    }

    @Override
    public void releaseFork() throws RemoteException
    {
        server.getPlates().get(server.getPlates().size()-1).getRightFork().release();
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
