package edu.hm.vss.model;

import edu.hm.vss.helper.Logger;
import edu.hm.vss.server.RMIServer;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by B3rni on 13.05.2015.
 */
public class Philosopher extends UnicastRemoteObject implements Serializable, Runnable
{
    private static final long serialVersionUID = 1L;

    private final int MEDITATIONTIME = 5;
    private final int SLEEPTIME = 10;
    private final int EATTIME = 1;

    private final TablePiece tablePiece;
    private final int index;
    private final boolean isVeryHungry;

    private int eatCounter = 0;
    private boolean run = true;
    private boolean allowedToEat = true;
    private String state;
    private int startIndex = -1;
    private boolean isFirstRound = true;

    public Philosopher(TablePiece tablePiece, int index, boolean hungry) throws RemoteException
    {
        this.tablePiece = tablePiece;
        this.index = index;
        isVeryHungry = hungry;
    }

    public Philosopher(TablePiece tablePiece, int index, boolean hungry, int eatCount) throws RemoteException
    {
        this(tablePiece, index, hungry);
        this.eatCounter = eatCount;
    }

    public Philosopher(TablePiece tablePiece, int index, boolean isHungry, int eatCount, int startIndex, boolean isFirstRound) throws RemoteException
    {
        this(tablePiece, index, isHungry, eatCount);
        this.startIndex = startIndex;
        this.isFirstRound = isFirstRound;
    }

    @Override
    public void run()
    {
        Plate plate;
        Fork leftFork;
        Fork rightFork;

        try
        {
            RMIServer.clientAPI.log(Philosopher.class.getSimpleName(), index + " spawning on tablePiece " + tablePiece.getIndex());
        } catch (RemoteException e)
        {
            e.printStackTrace();
        }

        eatLoop:
        while(run)
        {
            try
            {
                if(isAllowedToEat())
                {
                    state = "waiting for place";
                    plate = tablePiece.getPlate(this);
                    if(plate == null)
                    {
                        //Terminate own Thread
                        break eatLoop;
                    }
                    state = "got place";
                    leftFork = plate.getLeftFork();
                    rightFork = plate.getRightFork();
                    RMIServer.clientAPI.log(Philosopher.class.getSimpleName(), index + " leftForkindex: " + leftFork.getIndex() + " and " + rightFork.getIndex());
                    //Main.writeInDebugmode(this + " waiting for forks " + leftFork.getIndex() + " and " + rightFork.getIndex());
                    state = "waiting for Forks";
                    plate.waitForForks(this);
                    state = "got forks";
                    RMIServer.clientAPI.log(Philosopher.class.getSimpleName(), index + " got Forks " + leftFork.getIndex() + " and " + rightFork.getIndex());
                    //Main.writeInDebugmode(this + " got forks " + leftFork.getIndex() + " and " + rightFork.getIndex());
                    state = "start eating";
                    eat();
                    state = "releasing forks";
                    plate.releaseForks();
                    state = "releasing plate";
                    RMIServer.clientAPI.log(Philosopher.class.getSimpleName(), index + " release forks " + leftFork.getIndex() + " and " + rightFork.getIndex());
                            //Main.writeInDebugmode(this + " releases forks " + leftFork.getIndex() + " and " + rightFork.getIndex());
                    tablePiece.releasePlate(plate, this);
                    state = "plate released";
                    RMIServer.clientAPI.log(Philosopher.class.getSimpleName(), index + " released place " + plate.getIndex());
                    //Main.writeInDebugmode(this + " releases place " + plate.getIndex());
                    state = "meditating";
                    meditate();
                }
                else
                {
                    setAllowedToEat(true);
                }
            }
            catch (InterruptedException e)
            {
                System.err.println(this + " stop");
                run = false;
            }
            catch (RemoteException e)
            {
                // Client not reachable
            }
        }
        try
        {
            RMIServer.rightServerAPI.pushPhilosopher(index, isVeryHungry, eatCounter, startIndex, isFirstRound);
        } catch (RemoteException e)
        {
            try
            {
                RMIServer.clientAPI.neighbourUnreachable(e.getMessage());
                RMIServer.clientAPI.log(Philosopher.class.getSimpleName(), index + " killed on server " + tablePiece.getIndex());
            } catch (RemoteException e1)
            {
                //Terminate
            }
        }
    }

    private void meditate() throws InterruptedException, RemoteException
    {
        int meditationTime;
        if(isVeryHungry)
        {
            meditationTime = MEDITATIONTIME /2;
        }
        else
        {
            meditationTime = MEDITATIONTIME;
        }
        RMIServer.clientAPI.log(Philosopher.class.getSimpleName(), index + " " + this + (isVeryHungry ? " meditate short" : " meditate") + " (" + meditationTime + ")");
        //Main.writeInDebugmode(this + (isVeryHungry ? " meditate short" : " meditate") + " (" + meditationTime + ")");
        Thread. sleep(meditationTime);
    }

    private void eat() throws InterruptedException, RemoteException
    {
        RMIServer.clientAPI.log(Philosopher.class.getSimpleName(), index + " eating");
        //Main.writeInDebugmode(this + " eating");
        Thread.sleep(EATTIME);
        synchronized (this)
        {
            eatCounter++;
        }
        if (eatCounter % 3 == 2)
        {
            goSleeping();
        }
    }

    private void goSleeping() throws InterruptedException, RemoteException
    {
        RMIServer.clientAPI.log(Philosopher.class.getSimpleName(), index + " sleeping");
        //Main.writeInDebugmode(this + " sleeping");
        Thread.sleep(SLEEPTIME);
    }

    public int getIndex()
    {
        return index;
    }

    public int getEatCounter()
    {
        return eatCounter;
    }

    public synchronized void setAllowedToEat(boolean allowed)
    {
        allowedToEat = allowed;
    }

    public synchronized boolean isAllowedToEat()
    {
        return allowedToEat;
    }

    public String getStateOfPhilosopher()
    {
        return state;
    }

    public int getStartIndex() { return startIndex;}

    public void setStartIndex(int startIndex) { this.startIndex = startIndex;}

    public boolean isFirstRound() { return isFirstRound;}

    public void setIsFirstRound( boolean isFirstRound) { this.isFirstRound = isFirstRound;}

    public String toString()
    {
        return "Philosopher " + getIndex();
    }
}
