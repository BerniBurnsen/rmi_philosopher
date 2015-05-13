package edu.hm.vss.model;

import edu.hm.vss.interfaces.ITest;

import java.rmi.Remote;

/**
 * Created by Joncn on 13.05.2015.
 */
public class Test implements ITest
{

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
