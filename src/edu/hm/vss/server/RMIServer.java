package edu.hm.vss.server;

import edu.hm.vss.model.remoteMethodImpl;
import edu.hm.vss.interfaces.IPhilosopher;
import edu.hm.vss.interfaces.ITable;

import java.rmi.AlreadyBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by B3rni on 13.05.2015.
 */
public class RMIServer
{
    private static final int PORT = 1099;
    private static Registry registry;

    public static void startRegistry() throws RemoteException
    {
        registry = LocateRegistry.createRegistry(PORT);
    }

    public static void registerObject(String name, Remote remoteObject) throws RemoteException, AlreadyBoundException
    {
        registry.bind(name,remoteObject);
        System.out.println(name + " registered " + remoteObject.getClass().getName());
    }

    public static void initDiningPhilosophers()
    {
        int numberOfPhilosophers = 10;
        int numberOfHungryPhilosophers = 0;
        int numberOfPlaces = 10;
        Thread mainThread = Thread.currentThread();

        //System.out.println("Starting parallel philosophers with " + numberOfPhilosophers + " philosophers and " + numberOfPlaces + " places.");
        //System.out.println(numberOfHungryPhilosophers + " of them are very Hungry");

        //Generate the table
        ITable table = new Table(numberOfPlaces);

        //Generate the philosophers
        List<IPhilosopher> philosophers = new ArrayList<>();
        for(int i = 0; i < numberOfPhilosophers; i++)
        {
            philosophers.add(new Philosopher(table, i, i >= numberOfPhilosophers - numberOfHungryPhilosophers ? true : false));
        }

        //Generate the overseer
        Overseer overseer = new Overseer(philosophers, 10);

        //Start overseer and philosophers
        overseer.start();
        philosophers.forEach((philosopher) -> philosopher.start());

        //SekundCound
        Timer counter = new Timer();
        if(!DEBUG)
        {
            final long time = System.currentTimeMillis();
            counter.scheduleAtFixedRate(new TimerTask()
            {
                @Override
                public void run()
                {
                    long elapsed = (System.currentTimeMillis() - time) /1000;
                    System.out.print("\r" + ((elapsed < 10) ? " " + elapsed : elapsed) + " / " + DURATION + " seconds");
                    System.out.flush();
                }
            }, 0, 100);
        }

        //Stop philosophers and overseer
        new Timer().schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                if(!DEBUG)
                {
                    counter.cancel();
                    System.out.println("");
                }
                System.out.println("timer run out");
                for (Philosopher p : philosophers)
                {

                    p.interrupt();
                }
                overseer.interrupt();
            }
        }, DURATION * 1000);

        //Stop joining, if something went wrong
        new Timer().schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                System.out.println("timer run out");
                mainThread.interrupt();
            }
        }, (DURATION+2) * 1000);

        //Try to join termination of philosophers and overseer
        try
        {
            for(Philosopher p : philosophers)
            {
                p.join();
            }
            overseer.join();
        } catch (InterruptedException e)
        {

        }


        //Print out the final state
        System.out.println("================== TABLE ==================");
        System.out.println(table);

        System.out.println("================== STATS ==================");
        philosophers.forEach((p) -> System.out.println(p + " eats " + p.getEatcounter() + " times ::: STATE: " + p.getStateOfPhilosopher() ));

        System.exit(0);
    }

    public static void main(String[] args) throws Exception
    {
        startRegistry();
        registerObject("IPhilosopher", new remoteMethodImpl());
        initDiningPhilosophers();
        Thread.sleep(5 * 60 * 1000);
    }
}
