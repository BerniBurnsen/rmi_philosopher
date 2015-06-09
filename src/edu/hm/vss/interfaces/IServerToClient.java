package edu.hm.vss.interfaces;

import edu.hm.vss.helper.LogLevel;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * interface for the server to client communication
 * Is used for status updates
 */
public interface IServerToClient extends Remote, Serializable
{
    /**
     * updates the current eat count on the client.
     * @param philosopherIndex
     */
    void updateEatCount(int philosopherIndex, int eatCount) throws RemoteException;

    /**
     * tells the client that a neighbour is unreachable
     *
     */
    void neighbourUnreachable() throws RemoteException;

    /**
     * tells the client which philosopher is there
     * @param index
     */
    void registerPhilosopher(int index, int server) throws RemoteException;

    /**
     * helper to get the number of active instances
     * for the initialization process
     * @return the number of active instances
     * @throws RemoteException
     */
    int getNumberOfInstances() throws RemoteException;

    /**
     * Logging
     * @param level, the loglevel / category
     * @param type, the class/source of the message
     * @param message, the message itself
     * @throws RemoteException
     */
    void log(LogLevel level, String type, String message) throws RemoteException;
}
