package edu.hm.vss.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by B3rni on 20.05.2015.
 */
public interface IServerToClient extends Remote
{
    /**
     * updates the current eat count on the client.
     * @param philosopherIndex
     */
    void updateEatCount(int philosopherIndex, int eatCount) throws RemoteException;

    /**
     * tells the client that a neighbour is unreachable
     *
     * @param IP
     */
    void neighbourUnreachable(String IP) throws RemoteException;

    /**
     * tells the client which philosopher is there
     * @param index
     */
    void registerPhilosopher(int index, int server) throws RemoteException;
}
