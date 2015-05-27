package edu.hm.vss.model;

import edu.hm.vss.interfaces.ITest;

import java.io.Serializable;
import java.math.BigDecimal;
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
    public String doSomethingExpensive()
    {
        for (int i = 0; i < 8; i++)
        {
            new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    System.out.println("Thread " + Thread.currentThread().getName() + " started");
                    double val = 10;
                    for (; ; )
                    {
                        Math.atan(Math.sqrt(Math.pow(val, 10)));
                    }
                }
            }).start();
        }
        return "Los gehts";
    }
}
