package edu.hm.vss.interfaces;

import org.omg.stub.java.rmi._Remote_Stub;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by Joncn on 13.05.2015.
 */
public interface ITest extends Remote
{
    double doSomethingExpensive() throws RemoteException;
}
