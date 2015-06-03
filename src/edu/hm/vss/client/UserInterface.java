package edu.hm.vss.client;

import edu.hm.vss.userInterface.IUserInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by Joncn on 03.06.2015.
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
        if(hungry)
        {
            client.setNumberOfHungryPhilosophers(client.getNumberOfHungryPhilosophers() > 0 ? client.getNumberOfHungryPhilosophers()-1 : 0);

        }
        client.setNumberOfPhilosophers(client.getNumberOfPhilosophers() > 0 ? client.getNumberOfPhilosophers()-1 : 0);
        client.startFallback();
    }

    @Override
    public void addPhilosopher(boolean hungry) throws RemoteException
    {
        if(hungry)
        {
            client.setNumberOfHungryPhilosophers(client.getNumberOfHungryPhilosophers()+1);
        }
        client.setNumberOfPhilosophers(client.getNumberOfPhilosophers()+1);
        client.startFallback();
    }

    @Override
    public void removePlate() throws RemoteException
    {
        client.setNumberOfPlaces(client.getNumberOfPlaces() > 2 ? client.getNumberOfPlaces()-1 : 2);
        client.startFallback();
    }

    @Override
    public void addPlate() throws RemoteException
    {
        client.setNumberOfPlaces(client.getNumberOfPlaces()+1);
        client.startFallback();
    }
}
