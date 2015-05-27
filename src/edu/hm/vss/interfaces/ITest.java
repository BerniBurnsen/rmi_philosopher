package edu.hm.vss.interfaces;

import java.io.Serializable;
import java.math.BigDecimal;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by Joncn on 13.05.2015.
 */
public interface ITest extends Remote
{
    String doSomethingExpensive() throws RemoteException;
}
