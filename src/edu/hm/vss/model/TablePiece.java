package edu.hm.vss.model;

import edu.hm.vss.helper.LogLevel;
import edu.hm.vss.helper.Logger;
import edu.hm.vss.server.RMIServer;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Created by Joncn on 13.05.2015.
 */
public class TablePiece implements Serializable, Remote
{
    private List<Plate> plates;
    private int index;
    private int nextIndexToUse = 0;
    private RMIServer server;


    public TablePiece()
    {

    }

    public TablePiece(int index, List<Plate> plates, RMIServer server)
    {
        this.index = index;
        this.plates = plates;
        this.server = server;
    }

    public Plate getPlate(Philosopher p) throws InterruptedException, RemoteException
    {
        int startIndex = nextIndexToUse % plates.size();
        if(p.getStartIndex() == -1)
        {
            p.setStartIndex(plates.get(startIndex).getIndex());
        }
        else if(!p.isFirstRound())
        {
            startIndex = 0;
        }
        Plate plate = plates.get(startIndex);

        for (int i = startIndex; i < plates.size(); i++)
        {
            synchronized (plate)
            {
                if (!plate.isReserved())
                {
                    //Main.writeInDebugmode(p + " got " + plate);
                    plate.setIsReserved(true, p);
                    nextIndexToUse++;
                    return plate;
                }
            }

            if(server.getClientAPI().getNumberOfInstances() == 1)
            {
                if(i + 1 < plates.size())
                {
                    plate = plates.get(0);
                    i=0;
                    p.setIsFirstRound(false);
                }
                else
                {
                    plate = plates.get(i+1);
                }
            }
            else
            {
                plate = (i + 1 < plates.size()) ? plates.get((i + 1)) : null;
            }
            if(plate == null)
            {
                p.setIsFirstRound(false);
                nextIndexToUse++;
                return null;
            }
            else if(plate.getIndex() == p.getStartIndex() && !p.isFirstRound())
            {
                synchronized (plate)
                {
                    while(plate.isReserved())
                    {
                        plate.wait();
                    }
                    plate.setIsReserved(true, p);
                    nextIndexToUse++;
                    return plate;
                }
            }
            p.setIsFirstRound(false);
        }
        return null;
    }

    public void releasePlate(Plate plate, Philosopher p)
    {
        synchronized (plate)
        {
            //Main.writeInDebugmode(plate + " is now free");
            plate.setIsReserved(false, p);
            plate.notify();
        }
    }

    public int getIndex()
    {
        return index;
    }

    public String toString()
    {
        return "TablePiece " + index + " " + server.getInstanceNumber();
    }

    public List<Plate> getPlates()
    {
        return plates;
    }

    public void setPlates(List<Plate> plates)
    {
        this.plates = plates;
    }

    public void setIndex(int index)
    {
        this.index = index;
    }

    public int getNextIndexToUse()
    {
        return nextIndexToUse;
    }

    public void setNextIndexToUse(int nextIndexToUse)
    {
        this.nextIndexToUse = nextIndexToUse;
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
