package edu.hm.vss.server;

import edu.hm.vss.client.ServerToClient;
import edu.hm.vss.helper.Logger;
import edu.hm.vss.interfaces.IClientToServer;
import edu.hm.vss.interfaces.IServerToClient;
import edu.hm.vss.interfaces.IServerToServer;
import edu.hm.vss.interfaces.Settings;
import edu.hm.vss.model.*;

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by B3rni on 20.05.2015.
 */
public class ClientToServer implements IClientToServer
{
    private final RMIServer server;
    private final List<Thread> philosophers = new ArrayList<>();

    public ClientToServer(RMIServer server)
    {
        this.server = server;
    }
    @Override
    public boolean initConnections(String ClientIP, int ClientPort, String rightNeighbourIP, int rightNeighbourPort, String leftNeighbourIP, int leftNeighbourPort) throws RemoteException, NotBoundException
    {
        Registry registry;
        registry = LocateRegistry.getRegistry(ClientIP, ClientPort);
        server.setClientAPI((IServerToClient)registry.lookup(Settings.SERVER_TO_CLIENT));

        //only one instance
        if(server.getClientAPI().getNumberOfInstances() == 1)
        {
            server.getClientAPI().log(toString(), "initConnections - " + " only one instance");
            server.setRightServerAPI(null);
            server.setLeftServerAPI(null);
        }
        // only two instances
        else if(server.getClientAPI().getNumberOfInstances() == 2)
        {
            System.out.println("TEST");
            server.getClientAPI().log(toString(), "initConnections - " + "two instances");
            registry = LocateRegistry.getRegistry(rightNeighbourIP, rightNeighbourPort);
            server.setRightServerAPI((IServerToServer)registry.lookup(Settings.SERVER_TO_SERVER + (rightNeighbourPort - Settings.PORT_SERVER_BASE)));
            server.setLeftServerAPI(server.getRightServerAPI());
        }
        // > 2 instances
        else
        {
            server.getClientAPI().log(toString(), "initConnections - " + "more than two instances");
            registry = LocateRegistry.getRegistry(rightNeighbourIP, rightNeighbourPort);
            server.setRightServerAPI((IServerToServer)registry.lookup(Settings.SERVER_TO_SERVER + (rightNeighbourPort - Settings.PORT_SERVER_BASE)));
            registry = LocateRegistry.getRegistry(leftNeighbourIP, leftNeighbourPort);
            server.setLeftServerAPI((IServerToServer)registry.lookup(Settings.SERVER_TO_SERVER + (rightNeighbourPort - Settings.PORT_SERVER_BASE)));
        }
        return true;
    }


    @Override
    public boolean initServer(int seats, int maxSeats, int startIndex) throws RemoteException
    {
        server.getClientAPI().log(toString()," initServer - Seats: " + seats + " maxSeats " + maxSeats + " startIndex " + startIndex);
        for(int i = 0 ; i < seats ;i++)
        {
            Fork rightFork = new LocalFork(startIndex+i);

            Fork leftFork;

            if(i - 1 > 0 && server.getPlates().get(i - 1) != null && server.getPlates().get(i - 1).getRightFork() instanceof LocalFork)
            {
                leftFork = server.getPlates().get(i - 1).getRightFork();
            }
            else
            {
                leftFork = new RemoteFork((startIndex -1)%maxSeats, server);
            }

/*
            if(startIndex +i +1 < server.getPlates().size())
            {
                leftFork = startIndex + i + 1 == startIndex ? new RemoteFork(startIndex + i + 1,server) : server.getPlates().get(startIndex + i + 1).getRightFork();
            }
            else
            {
                leftFork = new RemoteFork(0,server);
            }*/
            server.getClientAPI().log(toString()," initServer - plate " + (startIndex + i) + " rightFork index " + rightFork.getIndex() + " leftFork isRemote: " + ((leftFork instanceof RemoteFork) ? "yes" : "no") + " index: " + leftFork.getIndex());
            server.getPlates().add(new Plate(leftFork, rightFork, startIndex + i));
        }
        server.setTablePiece(new TablePiece(server.getInstanceNumber(), server.getPlates(),server));
        return true;
    }

    @Override
    public boolean createNewPhilosopher(int index, boolean hungry) throws RemoteException
    {
        server.getClientAPI().log(toString(), "createNewP - " + index);
        philosophers.add(new Thread(new Philosopher(server, server.getTablePiece(), index, hungry)));
        server.getClientAPI().registerPhilosopher(index, server.getInstanceNumber());
        return true;
    }

    @Override
    public void startPhilosophers() throws RemoteException
    {
        server.getClientAPI().log(toString(), "START");
        for(Thread p : philosophers)
        {
            p.start();
        }
    }

    @Override
    public boolean respawnPhilosopher(int index, boolean hungry, int eatCount) throws RemoteException
    {
        server.getClientAPI().log(toString(), "respawnP - " + index + " hungry: " + hungry + " eatCount" + eatCount);
        new Thread(new Philosopher(server,server.getTablePiece(), index, hungry, eatCount)).start();
        server.getClientAPI().registerPhilosopher(index, server.getInstanceNumber());
        return true;
    }

    @Override
    public void stopServer() throws RemoteException
    {
        server.getClientAPI().log(toString(), "stopServer - ");
    }

    @Override
    public void punishPhilosopher(int index) throws RemoteException
    {
        server.getClientAPI().log(toString(), "punishPhil " + index);
    }

    @Override
    public boolean isReachable()
    {
        return false;
    }

    public String toString()
    {
        return "ClientToServer "+ server.getInstanceNumber();
    }
}
