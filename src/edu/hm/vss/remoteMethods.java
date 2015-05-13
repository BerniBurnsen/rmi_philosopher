package edu.hm.vss;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by B3rni on 13.05.2015.
 */
public interface remoteMethods extends Remote
{
    String sayHello() throws RemoteException;
}
