package edu.hm.vss.interfaces;

import edu.hm.vss.model.ForkToken;

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

    /**
     * requests a fork (token) on a neighbour server
     * @return
     */
    ForkToken requestForkToken() throws RemoteException;

    boolean requestIsRemoteForkReserved() throws RemoteException;

    void setIsForkReserved(int index, boolean isReserved) throws RemoteException;
}
