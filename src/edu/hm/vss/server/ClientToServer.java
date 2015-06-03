package edu.hm.vss.server;

import edu.hm.vss.client.ServerToClient;
import edu.hm.vss.helper.LogLevel;
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
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by B3rni on 20.05.2015.
 */
public class ClientToServer extends UnicastRemoteObject implements IClientToServer
{
    private RMIServer server;

    public ClientToServer() throws RemoteException
    {
        super();

    }

    public ClientToServer(RMIServer server) throws RemoteException
    {
        super();
        this.server = server;
        System.out.println("ClientToServer contructor");
    }

    @Override
    public boolean initClientConnection(String clientIP, int clientPort) throws RemoteException, NotBoundException
    {
        Registry registry;
        registry = LocateRegistry.getRegistry(clientIP, clientPort);
        if(server.getClientAPI() == null)
        {
            server.setClientAPI((IServerToClient) registry.lookup(Settings.SERVER_TO_CLIENT));
        }
        server.getClientAPI().log(LogLevel.INIT, toString(), "initClientConnection from Server " + server.getInstanceNumber() + " to Client");
        return true;
    }

    @Override
    public boolean initServerConnections(String rightNeighbourIP, int rightNeighbourPort, String leftNeighbourIP, int leftNeighbourPort) throws RemoteException, NotBoundException
    {
        Registry serverRegistry;
        //only one instance
        if(server.getClientAPI().getNumberOfInstances() == 1)
        {
            server.getClientAPI().log(LogLevel.INIT, toString(), "initConnections - " + " only one instance");
            server.setRightServerAPI(null);
            server.setLeftServerAPI(null);
        }
        // only two instances
        else if(server.getClientAPI().getNumberOfInstances() == 2)
        {
            System.out.println("TEST");
            server.getClientAPI().log(LogLevel.INIT, toString(), "initConnections - " + "two instances");
            serverRegistry = LocateRegistry.getRegistry(rightNeighbourIP, rightNeighbourPort);
            server.setRightServerAPI((IServerToServer)serverRegistry.lookup(Settings.SERVER_TO_SERVER + (rightNeighbourPort - Settings.PORT_SERVER_BASE)));
            server.setLeftServerAPI(server.getRightServerAPI());
        }
        // > 2 instances
        else
        {
            server.getClientAPI().log(LogLevel.INIT, toString(), "initConnections - " + "more than two instances");
            serverRegistry = LocateRegistry.getRegistry(rightNeighbourIP, rightNeighbourPort);
            server.setRightServerAPI((IServerToServer)serverRegistry.lookup(Settings.SERVER_TO_SERVER + (rightNeighbourPort - Settings.PORT_SERVER_BASE)));
            serverRegistry = LocateRegistry.getRegistry(leftNeighbourIP, leftNeighbourPort);
            server.setLeftServerAPI((IServerToServer)serverRegistry.lookup(Settings.SERVER_TO_SERVER + (rightNeighbourPort - Settings.PORT_SERVER_BASE)));
        }

        if(server.getRightServerAPI() != null)
        {
            return server.getLeftServerAPI().testConnection() && server.getRightServerAPI().testConnection();
        }
        else
        {
            return true;
        }
    }

    @Override
    public boolean initServer(int seats, int maxSeats, int startIndex) throws RemoteException
    {
        server.getPhilosophers().clear();
        server.getPlates().clear();
        server.setTablePiece(null);

        server.getClientAPI().log(LogLevel.INIT, toString(), " initServer - Seats: " + seats + " maxSeats " + maxSeats + " startIndex " + startIndex);
        for(int i = 0 ; i < seats ;i++)
        {
            Fork rightFork = new LocalFork(startIndex+i);

            Fork leftFork = null;

            if( (i - 1) >= 0 && server.getPlates().get(i - 1) != null && server.getPlates().get(i - 1).getRightFork() instanceof LocalFork)
            {
                leftFork = server.getPlates().get(i - 1).getRightFork();
            }
            else if( seats != maxSeats)
            {
                leftFork = new RemoteFork(startIndex - 1 < 0 ? maxSeats-1 :(startIndex -1)%maxSeats, server);
            }
            if(leftFork != null)
            {
                server.getClientAPI().log(LogLevel.INIT, toString(), " initServer - plate " + (startIndex + i) + " rightFork index " + rightFork.getIndex() + " leftFork isRemote: " + ((leftFork instanceof RemoteFork) ? "yes" : "no") + " index: " + leftFork.getIndex());
            }
            server.getPlates().add(new Plate(leftFork, rightFork, startIndex + i));
        }
        if(seats == maxSeats)
        {
            server.getPlates().get(0).setLeftFork(server.getPlates().get(seats -1).getRightFork());
            server.getClientAPI().log(LogLevel.INIT, toString(), " initServer - plate 0 rightFork index " + 0 + " leftFork isRemote: no");
        }
        server.setTablePiece(new TablePiece(server.getInstanceNumber(), server.getPlates(), server));
        server.setRun(true);
        return true;
    }

    @Override
    public boolean createNewPhilosopher(int index, boolean hungry) throws RemoteException
    {
        server.getClientAPI().log(LogLevel.INIT, toString(), "createNewP - " + index);
        server.getPhilosophers().put(index, new Philosopher(server, server.getTablePiece(), index, hungry));
        server.getClientAPI().registerPhilosopher(index, server.getInstanceNumber());
        return true;
    }

    @Override
    public void startPhilosophers() throws RemoteException
    {
        server.getClientAPI().log(LogLevel.INIT, toString(), "--------START----------");
        for(Philosopher p : server.getPhilosophers().values())
        {
            server.getClientAPI().log(LogLevel.INIT, toString(), "starting " + p);
            p.start();
        }
    }

    @Override
    public boolean respawnPhilosopher(int index, boolean hungry, int eatCount) throws RemoteException
    {
        server.getClientAPI().log(LogLevel.FALLBACK, toString(), "respawnP - " + index + " hungry: " + hungry + " eatCount: " + eatCount);
        Philosopher p = new Philosopher(server,server.getTablePiece(), index, hungry, eatCount);
        server.getPhilosophers().put(index, p);
        server.getClientAPI().registerPhilosopher(index, server.getInstanceNumber());
        return true;
    }

    @Override
    public void stopServer() throws RemoteException
    {
        boolean running = server.isRun();
        server.setRun(false);

        for (Philosopher p : server.getPhilosophers().values())
        {
            System.out.println("interrupt " + p);
            p.interrupt();
        }
        for (Philosopher p : server.getPhilosophers().values())
        {
            try
            {
                p.join(200);
                System.out.println("joined on " + p);
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
        try
        {
            Thread.sleep(1 * 1000);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        if(running)
        {
            server.getClientAPI().log(LogLevel.INIT, toString(), "-------STOPPING------");
            for (Philosopher p : server.getPhilosophers().values())
            {
                server.getClientAPI().log(LogLevel.INIT, toString(), p + " eats " + p.getEatCounter() + " times ::: STATE: " + p.getCurrentState());
            }
            server.getClientAPI().log(LogLevel.INIT, toString(), "");
            server.getClientAPI().log(LogLevel.INIT, toString(), "Current number of Philosophers on Server: " + server.getPhilosophers().size());
            server.getClientAPI().log(LogLevel.INIT, toString(), "");
            server.getClientAPI().log(LogLevel.INIT, toString(), "-------STOPPING------");
        }
    }

    @Override
    public void punishPhilosopher(int index) throws RemoteException
    {
        Philosopher p =  server.getPhilosophers().get(index);
        if(p != null && p.isAllowedToEat())
        {
            p.setAllowedToEat(false);
            server.getClientAPI().log(LogLevel.SERVER, toString(), "punishPhil " + index);
        }
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

    public RMIServer getServer()
    {

        return server;
    }

    public void setServer(RMIServer server)
    {
        this.server = server;
    }
}
