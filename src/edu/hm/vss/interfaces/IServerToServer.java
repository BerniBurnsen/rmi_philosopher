package edu.hm.vss.interfaces;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by B3rni on 20.05.2015.
 */
public interface IServerToServer extends Remote, Serializable
{
    /**
     * there are no empty seats, push to next server to look for seats
     * @param index
     * @param isHungry
     * @param eatCount
     */
    void pushPhilosopher(int index, boolean isHungry, int eatCount, int startIndex, boolean isFirstRound) throws RemoteException;

    boolean testConnection() throws RemoteException;

    boolean tryToGetFork() throws RemoteException;

    void waitForFork() throws RemoteException;

    void releaseFork() throws RemoteException;
}
