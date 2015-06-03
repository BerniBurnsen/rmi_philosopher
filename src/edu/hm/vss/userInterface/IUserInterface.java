package edu.hm.vss.userInterface;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by Joncn on 03.06.2015.
 */
public interface IUserInterface extends Remote, Serializable
{
    void removePhilosopher(boolean hungry) throws RemoteException;

    void addPhilosopher(boolean hungry) throws RemoteException;

    void removePlate() throws RemoteException;

    void addPlate() throws RemoteException;
}
