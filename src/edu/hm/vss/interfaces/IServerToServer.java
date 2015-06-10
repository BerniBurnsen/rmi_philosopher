package edu.hm.vss.interfaces;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * interface for the server to server communication.
 * Used to move philosophers across the server and to request/release forks which are located remotely.
 */
public interface IServerToServer extends Remote, Serializable
{
    /**
     * there are no empty seats, push to next server to look for seats
     * @param index
     * @param isHungry
     * @param eatCount
     */
    boolean pushPhilosopher(int index, boolean isHungry, int eatCount, int startIndex, boolean isFirstRound) throws RemoteException;

    boolean testConnection() throws RemoteException;

    /**
     * requests a fork on the neighbour server
     * @return
     * @throws RemoteException
     * @throws InterruptedException
     */
    boolean tryToGetFork() throws RemoteException, InterruptedException;

    /**
     * wait for fork on the neighbour server
     * @throws RemoteException
     * @throws InterruptedException
     */
    void waitForFork() throws RemoteException, InterruptedException;

    /**
     * release fork on the neighbour server.
     * @throws RemoteException
     */
    void releaseFork() throws RemoteException;
}