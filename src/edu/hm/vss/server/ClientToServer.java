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
        if(RMIServer.clientAPI.getNumberOfInstances() == 1)
        {
            RMIServer.clientAPI.log(ClientToServer.class.getSimpleName() + RMIServer.instanceNumber, "initConnections - " + " only one instance");
            RMIServer.rightServerAPI = RMIServer.leftServerAPI = null;
        }
        // only two instances
        else if(RMIServer.clientAPI.getNumberOfInstances() == 2)
        {
            RMIServer.clientAPI.log(ClientToServer.class.getSimpleName(), "initConnections - " + "two instances");
            registry = LocateRegistry.getRegistry(rightNeighbourIP, rightNeighbourPort);
            RMIServer.rightServerAPI = RMIServer.leftServerAPI = (IServerToServer)registry.lookup(Settings.SERVER_TO_SERVER);
        }
        // > 2 instances
        else
        {
            RMIServer.clientAPI.log(ClientToServer.class.getSimpleName(), "initConnections - " + "more than two instances");
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
        //logger.printLog(ClientToServer.class.getSimpleName()," initServer - Seats: " + seats + " maxSeats " + maxSeats + " startIndex " + startIndex);
        for(int i = 0 ; i < seats ;i++)
        {
            Fork rightFork = new LocalFork(startIndex+i);

            Fork leftFork;
            if(startIndex +i-1 >= 0)
            {
                leftFork = startIndex + i == startIndex ? new RemoteFork(startIndex + i - 1) : RMIServer.plates.get(startIndex + i - 1).getRightFork();
            }
            else
            {
                leftFork = new RemoteFork(maxSeats-1);
            }
            //logger.printLog(ClientToServer.class.getSimpleName()," initServer - plate " + (startIndex + i) + " rightFork index " + rightFork.getIndex() + " leftFork isRemote: " + ((leftFork instanceof RemoteFork) ? "yes" : "no") + " index: " + leftFork.getIndex());
            RMIServer.plates.add(new Plate(leftFork, rightFork, startIndex + i));
        }
        RMIServer.tablePiece = new TablePiece(RMIServer.instanceNumber, RMIServer.plates);
        return true;
    }

    @Override
    public boolean createNewPhilosopher(int index, boolean hungry) throws RemoteException
    {
        RMIServer.clientAPI.log(ClientToServer.class.getSimpleName(), "createNewP - " + index);
        new Thread(new Philosopher(RMIServer.tablePiece, index, hungry)).start();
        RMIServer.clientAPI.registerPhilosopher(index, RMIServer.instanceNumber);
        return true;
    }

    @Override
    public boolean respawnPhilosopher(int index, boolean hungry, int eatCount) throws RemoteException
    {
        RMIServer.clientAPI.log(ClientToServer.class.getSimpleName(), "respawnP - " + index + " hungry: " + hungry + " eatCount" + eatCount);
        new Thread(new Philosopher(RMIServer.tablePiece, index, hungry, eatCount)).start();
        RMIServer.clientAPI.registerPhilosopher(index, RMIServer.instanceNumber);
        return true;
    }

    @Override
    public void stopServer() throws RemoteException
    {
        RMIServer.clientAPI.log(ClientToServer.class.getSimpleName(), "stopServer - ");
    }

    @Override
    public void punishPhilosopher(int index) throws RemoteException
    {
        RMIServer.clientAPI.log(ClientToServer.class.getSimpleName(), "punishPhil " + index);
    }

    @Override
    public boolean isReachable()
    {
        return false;
    }
}
