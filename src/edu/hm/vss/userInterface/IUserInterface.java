package edu.hm.vss.userInterface;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * interface for user interaction.
 */
public interface IUserInterface extends Remote, Serializable
{
    /**
     * removes a philosopher from the setup
     * @param hungry
     * @throws RemoteException
     */
    void removePhilosopher(boolean hungry) throws RemoteException;

    /**
     * adds new philosopher from the setup
     * @param hungry
     * @throws RemoteException
     */
    void addPhilosopher(boolean hungry) throws RemoteException;

    /**
     * remove a place/plate from the setup
     * @throws RemoteException
     */
    void removePlate() throws RemoteException;

    /**
     * adds a new plate to the setup
     * @throws RemoteException
     */
    void addPlate() throws RemoteException;
}