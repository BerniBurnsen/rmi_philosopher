package edu.hm.vss.model;

import edu.hm.vss.helper.Logger;
import edu.hm.vss.server.RMIServer;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by B3rni on 13.05.2015.
 */
public class Philosopher extends Thread implements Serializable
{
    private static final long serialVersionUID = 1L;

    private final int MEDITATIONTIME = 5;
    private final int SLEEPTIME = 10;
    private final int EATTIME = 1;

    private  TablePiece tablePiece;
    private  int index;
    private  boolean isVeryHungry;

    private int eatCounter = 0;
    private boolean run = true;
    private boolean allowedToEat = true;
    private String state;
    private int startIndex = -1;
    private boolean isFirstRound = true;

    private RMIServer server;

    public Philosopher()
    {

    }

    public Philosopher(RMIServer server, TablePiece tablePiece, int index, boolean hungry) throws RemoteException
    {
        this.server = server;
        this.tablePiece = tablePiece;
        this.index = index;
        isVeryHungry = hungry;
    }

    public Philosopher(RMIServer server,TablePiece tablePiece, int index, boolean hungry, int eatCount) throws RemoteException
    {
        this(server,tablePiece, index, hungry);
        this.eatCounter = eatCount;
    }

    public Philosopher(RMIServer server,TablePiece tablePiece, int index, boolean isHungry, int eatCount, int startIndex, boolean isFirstRound) throws RemoteException
    {
        this(server, tablePiece, index, isHungry, eatCount);
        this.startIndex = startIndex;
        this.isFirstRound = isFirstRound;
    }

    @Override
    public void run()
    {
        Plate plate;
        Fork leftFork;
        Fork rightFork;

        new Thread(new Runnable() {

            @Override
            public void run()
            {
                while(run)
                {
                    try
                    {
                        server.getClientAPI().updateEatCount(index, eatCounter);
                        Thread.sleep(2 * 1000);
                    } catch (InterruptedException e)
                    {
                        run = false;
                    } catch (RemoteException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        try
        {
            server.getClientAPI().log(toString(), index + " spawning on tablePiece " + tablePiece.getIndex());
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
                    server.getClientAPI().log(toString(), index + " leftForkindex: " + leftFork.getIndex() + " and " + rightFork.getIndex());
                    //Main.writeInDebugmode(this + " waiting for forks " + leftFork.getIndex() + " and " + rightFork.getIndex());
                    state = "waiting for Forks";
                    plate.waitForForks(this);
                    state = "got forks";
                    server.getClientAPI().log(toString(), index + " got Forks " + leftFork.getIndex() + " and " + rightFork.getIndex());
                    //Main.writeInDebugmode(this + " got forks " + leftFork.getIndex() + " and " + rightFork.getIndex());
                    state = "start eating";
                    eat();
                    state = "releasing forks";
                    plate.releaseForks();
                    state = "releasing plate";
                    server.getClientAPI().log(toString(), index + " release forks " + leftFork.getIndex() + " and " + rightFork.getIndex());
                            //Main.writeInDebugmode(this + " releases forks " + leftFork.getIndex() + " and " + rightFork.getIndex());
                    tablePiece.releasePlate(plate, this);
                    state = "plate released";
                    server.getClientAPI().log(toString(), index + " released place " + plate.getIndex());
                    //Main.writeInDebugmode(this + " releases place " + plate.getIndex());
                    state = "meditating";
                    meditate();
                }
                else
                {
                    goSleeping();
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
        if(run)
        {
            try
            {
                if(server.getRightServerAPI().pushPhilosopher(index, isVeryHungry, eatCounter, startIndex, isFirstRound))
                {
                    server.getPhilosophers().remove(index);
                    run = false;
                }
            } catch (RemoteException e)
            {
                try
                {
                    server.getClientAPI().neighbourUnreachable(e.getMessage());
                    server.getClientAPI().log(toString(), index + " killed on server " + tablePiece.getIndex());
                } catch (RemoteException e1)
                {
                    //Terminate
                }
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
        server.getClientAPI().log(toString(), index + " " + this + (isVeryHungry ? " meditate short" : " meditate") + " (" + meditationTime + ")");
        //Main.writeInDebugmode(this + (isVeryHungry ? " meditate short" : " meditate") + " (" + meditationTime + ")");
        Thread. sleep(meditationTime);
    }

    private void eat() throws InterruptedException, RemoteException
    {
        server.getClientAPI().log(toString(), index + " eating");
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
        server.getClientAPI().log(toString(), index + " sleeping");
        //Main.writeInDebugmode(this + " sleeping");
        Thread.sleep(SLEEPTIME);
    }

    @Override
    public void interrupt()
    {
        run = false;
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
        return "Philosopher " + getIndex() + " " + server.getInstanceNumber();
    }

    public static long getSerialVersionUID()
    {
        return serialVersionUID;
    }

    public int getMEDITATIONTIME()
    {
        return MEDITATIONTIME;
    }

    public int getSLEEPTIME()
    {
        return SLEEPTIME;
    }

    public int getEATTIME()
    {
        return EATTIME;
    }

    public TablePiece getTablePiece()
    {
        return tablePiece;
    }

    public void setTablePiece(TablePiece tablePiece)
    {
        this.tablePiece = tablePiece;
    }

    public void setIndex(int index)
    {
        this.index = index;
    }

    public boolean isVeryHungry()
    {
        return isVeryHungry;
    }

    public void setIsVeryHungry(boolean isVeryHungry)
    {
        this.isVeryHungry = isVeryHungry;
    }

    public void setEatCounter(int eatCounter)
    {
        this.eatCounter = eatCounter;
    }

    public boolean isRun()
    {
        return run;
    }

    public void setRun(boolean run)
    {
        this.run = run;
    }

    public String getCurrentState()
    {
        return state;
    }

    public void setState(String state)
    {
        this.state = state;
    }

    public RMIServer getServer()
    {
        return server;
    }

    public void setServer(RMIServer server)
    {
        this.server = server;
    }

}
