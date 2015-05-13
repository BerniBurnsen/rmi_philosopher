package edu.hm.vss.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by B3rni on 13.05.2015.
 */
public interface IPhilosopher extends Remote
{
    int getIndex();
    int getEatCounter();
}
