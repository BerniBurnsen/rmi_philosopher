package edu.hm.vss.interfaces;

import edu.hm.vss.helper.LogLevel;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by B3rni on 20.05.2015.
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

    int getNumberOfInstances() throws RemoteException;

    void log(LogLevel level, String type, String message) throws RemoteException;
}
