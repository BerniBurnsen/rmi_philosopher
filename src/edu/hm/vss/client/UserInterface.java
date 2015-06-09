package edu.hm.vss.client;

import edu.hm.vss.userInterface.IUserInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * implementation of the userinterface.
 * Adds, remove philosohpers and plates, after that a setup rebuilt is started.
 */
public class UserInterface extends UnicastRemoteObject implements IUserInterface
{
    private Client client;
    private boolean connectionError = false;

    public UserInterface() throws RemoteException
    {
        super();
    }

    public UserInterface(Client client) throws RemoteException
    {
        super();
        this.client = client;
        System.out.println("UserInterface constructor");
    }

    @Override
    public void removePhilosopher(boolean hungry) throws RemoteException
    {
        client.stopAll();
        if(hungry)
        {
            client.setNumberOfHungryPhilosophers(client.getNumberOfHungryPhilosophers() > 0 ? client.getNumberOfHungryPhilosophers()-1 : 0);
            client.getAllEatCounts().remove(client.getAllEatCounts().size()-1);
        }
        else
        {
            client.getAllEatCounts().remove(0);
            int i = 0;
            Map<Integer, Integer> newEatcounts = new ConcurrentHashMap<>();
            for(Map.Entry<Integer, Integer> eCount : client.getAllEatCounts().entrySet())
            {
                newEatcounts.put(eCount.getKey()-1, eCount.getValue());
            }
            client.setAllEatCounts(newEatcounts);
        }
        client.setNumberOfPhilosophers(client.getNumberOfPhilosophers() > 0 ? client.getNumberOfPhilosophers()-1 : 0);
        client.startAgain();
    }

    @Override
    public void addPhilosopher(boolean hungry) throws RemoteException
    {
        client.stopAll();
        if(hungry)
        {
            client.setNumberOfHungryPhilosophers(client.getNumberOfHungryPhilosophers()+1);
        }
        client.setNumberOfPhilosophers(client.getNumberOfPhilosophers()+1);
        client.startAgain();
    }

    @Override
    public void removePlate() throws RemoteException
    {
        client.stopAll();
        client.setNumberOfPlaces(client.getNumberOfPlaces() > 2 ? client.getNumberOfPlaces() - 1 : 2);
        client.startAgain();
    }

    @Override
    public void addPlate() throws RemoteException
    {
        client.stopAll();
        client.setNumberOfPlaces(client.getNumberOfPlaces()+1);
        client.startAgain();
    }
}
