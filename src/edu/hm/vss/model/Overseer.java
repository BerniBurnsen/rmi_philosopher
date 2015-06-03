package edu.hm.vss.model;

import edu.hm.vss.client.Client;
import edu.hm.vss.helper.LogLevel;
import edu.hm.vss.interfaces.IClientToServer;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Overseer extends Thread
{
    private Client client;
    private int maxDifferenz;
    private List<IClientToServer> servers;

    private boolean run = false;

    public Overseer(Client client, int maxDifferenz)
    {
        this.client = client;
        servers = new ArrayList<>(client.getServers());
        this.maxDifferenz = maxDifferenz;
    }

    @Override
    public void run()
    {
        run = true;
        int[] pCount = new int[client.getNumberOfPhilosophers()];
        client.getLogger().printLog(LogLevel.OVERSEER, toString(), "Overseer started");
        runLoop:
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
                        for(IClientToServer server : servers)
                        {
                            client.getLogger().printLog(LogLevel.OVERSEER, toString(), "Punisch Philosoper " + i);
                            server.punishPhilosopher(i);
                        }
                    } catch (RemoteException e)
                    {
                        //nothing to do
                    }
                }
            }
            try
            {
                Thread.sleep(5);
            } catch (InterruptedException e)
            {
                System.err.println(e);
                run = false;
            }
        }
        client.getLogger().printLog(LogLevel.OVERSEER, toString(), "Overseer stopped");
    }

    @Override
    public void interrupt()
    {
        super.interrupt();
        run = false;
    }

    @Override
    public String toString()
    {
        return Overseer.class.getSimpleName();
    }
}