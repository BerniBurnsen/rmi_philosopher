package edu.hm.vss.model;

import edu.hm.vss.helper.LogLevel;
import edu.hm.vss.server.RMIServer;

import java.rmi.RemoteException;
import java.util.List;

/**
 * Tablepieces are usually distributed across several servers
 */
public class TablePiece
{
    private List<Plate> plates;
    private int index;
    private RMIServer server;

    public TablePiece(int index, List<Plate> plates, RMIServer server)
    {
        this.index = index;
        this.plates = plates;
        this.server = server;
    }

    public Plate getPlate(Philosopher p) throws InterruptedException, RemoteException
    {
        int startIndex = p.getIndex() % plates.size();
        if(p.getStartIndex() == -1)
        {
            p.setStartIndex(plates.get(startIndex).getIndex());
        }
        else if(!p.isFirstRound())
        {
            server.getClientAPI().log(LogLevel.TABLE, toString(), p + " is first round: " +p.isFirstRound());
            startIndex = 0;
        }
        Plate plate = plates.get(startIndex);

        server.getClientAPI().log(LogLevel.TABLE, toString(), p + " startIndex " + startIndex);

        for (int i = startIndex; i < plates.size(); i++)
        {
            synchronized (plate)
            {
                if (!plate.isReserved())
                {
                    server.getClientAPI().log(LogLevel.TABLE, toString(), p + " got Plate " + plate.getIndex());
                    plate.setIsReserved(true, p);
                    return plate;
                }
            }

            if(server.getClientAPI().getNumberOfInstances() == 1)
            {
                if(i + 1 >= plates.size())
                {
                    plate = plates.get(0);
                    i=0;
                    p.setIsFirstRound(false);
                }
                else
                {
                    server.getClientAPI().log(LogLevel.TABLE, toString(), p + " got no Place, go to " + i+1);
                    plate = plates.get(i+1);
                }
            }
            else
            {
                plate = (i + 1 < plates.size()) ? plates.get((i + 1)) : null;
            }

            if(plate == null)
            {
                server.getClientAPI().log(LogLevel.TABLE, toString(), p + " got no Place, go to next Server");
                p.setIsFirstRound(false);
                return null;
            }
            else if(plate.getIndex() == p.getStartIndex() && !p.isFirstRound())
            {
                synchronized (plate)
                {
                    while(plate.isReserved())
                    {
                        server.getClientAPI().log(LogLevel.TABLE, toString(), p + " waiting for Plate " + plate.getIndex());
                        plate.wait();
                    }
                    plate.setIsReserved(true, p);
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

    public void setIndex(int index)
    {
        this.index = index;
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