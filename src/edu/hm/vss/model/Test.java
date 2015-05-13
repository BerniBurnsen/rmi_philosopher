package edu.hm.vss.model;

import edu.hm.vss.interfaces.ITest;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by Joncn on 13.05.2015.
 */
public class Test extends UnicastRemoteObject implements ITest, Serializable
{

    public Test() throws RemoteException
    {
    }

    @Override
    public double doSomethingExpensive()
    {
        double result = 0;
        for(int i = 0; i < 1000 * 1000; i++)
        {
          result += Math.sqrt(Math.random() / (Math.random() + 1) * 500);
        }
        return result;
    }
}
