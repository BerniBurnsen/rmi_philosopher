package edu.hm.vss.server;

import edu.hm.vss.client.ServerToClient;
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

/**
 * Created by B3rni on 20.05.2015.
 */
public class ClientToServer implements IClientToServer
{



    @Override
    public boolean initConnections(String ClientIP, int ClientPort, String rightNeighbourIP, int rightNeighbourPort, String leftNeighbourIP, int leftNeighbourPort) throws RemoteException, NotBoundException
    {
        Registry registry;
        registry = LocateRegistry.getRegistry(ClientIP, ClientPort);
        RMIServer.clientAPI = (IServerToClient)registry.lookup(Settings.SERVER_TO_CLIENT);

        //only one instance
        if(Settings.PORT_SERVER_BASE +RMIServer.instanceNumber == leftNeighbourPort)
        {
            RMIServer.rightServerAPI = RMIServer.leftServerAPI = null;
        }
        // only two instances
        else if(rightNeighbourPort == leftNeighbourPort)
        {
            registry = LocateRegistry.getRegistry(rightNeighbourIP, rightNeighbourPort);
            RMIServer.rightServerAPI = RMIServer.leftServerAPI = (IServerToServer)registry.lookup(Settings.SERVER_TO_SERVER);
        }
        // > 2 instances
        else
        {
            registry = LocateRegistry.getRegistry(rightNeighbourIP, rightNeighbourPort);
            RMIServer.rightServerAPI = (IServerToServer)registry.lookup(Settings.SERVER_TO_SERVER);
            registry = LocateRegistry.getRegistry(leftNeighbourIP, leftNeighbourPort);
            RMIServer.leftServerAPI = (IServerToServer)registry.lookup(Settings.SERVER_TO_SERVER);
        }
        return true;
    }


    @Override
    public boolean initServer(int seats, int maxSeats, int startIndex)
    {

        for(int i = 0 ; i < seats ;i++)
        {
            Fork rightFork = new LocalFork(startIndex+i);

            Fork leftFork = startIndex+i == startIndex ? new RemoteFork(startIndex+i-1) : RMIServer.plates.get(startIndex+i-1).getRightFork();
            RMIServer.plates.add(new Plate(startIndex+i));
        }
        RMIServer.tablePiece = new TablePiece(RMIServer.plates);
        return true;
    }

    @Override
    public boolean createNewPhilosopher(int index, boolean hungry)
    {

        return false;
    }

    @Override
    public boolean respawnPhilosopher(int index, boolean hungry, int eatCount)
    {
        return false;
    }

    @Override
    public void stopServer()
    {

    }

    @Override
    public void punishPhilosopher(int index)
    {

    }

    @Override
    public boolean isReachable()
    {
        return false;
    }
}
