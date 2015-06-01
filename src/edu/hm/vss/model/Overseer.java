package edu.hm.vss.model;

import edu.hm.vss.client.Client;

import java.rmi.RemoteException;
import java.util.Map;

public class Overseer extends Thread
{
    private Client client;
    private int maxDifferenz;

    private boolean run = false;

    public Overseer(Client client, int maxDifferenz)
    {
        this.client = client;
        this.maxDifferenz = maxDifferenz;
    }

    @Override
    public void run()
    {
        run = true;
        int[] pCount = new int[client.getNumberOfPhilosophers()];
        while(run)
        {
            int minCount = Integer.MAX_VALUE;

            for(Map.Entry<Integer, Integer> e : client.getAllEatCounts().entrySet())
            {
                pCount[e.getKey()] = e.getValue();
            }

            for(int i = 0; i < pCount.length; i++)
            {
                if(pCount[i] < minCount)
                {
                    minCount = pCount[i];
                }
            }

            for(int i = 0; i < pCount.length; i++)
            {
                if(pCount[i] >= (minCount + maxDifferenz))
                {
                    try
                    {
                        client.getServers().get(client.getLocationMap().get(i)).punishPhilosopher(i);
                    } catch (RemoteException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void interrupt()
    {
        run = false;
    }
}
