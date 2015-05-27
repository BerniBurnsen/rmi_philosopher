package edu.hm.vss.model;

import java.io.Serializable;
import java.rmi.Remote;
import java.util.List;

/**
 * Created by Joncn on 13.05.2015.
 */
public class TablePiece implements Serializable, Remote
{
    private List<Plate> plates;
    public TablePiece(List<Plate> plates)
    {
        this.plates = plates;
    }
    int nextIndexToUse = 0;

    public Plate getPlate(Philosopher p) throws InterruptedException
    {
        int startIndex = nextIndexToUse % plates.size();
        if(p.getStartIndex() == -1)
        {
            p.setStartIndex(plates.get(startIndex).getIndex());
        }
        else
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
            plate = (i + 1 < plates.size()) ? plates.get((i + 1)) : null;
            if(plate == null)
            {
                p.setIsFirstRound(false);
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
                    return plate;
                }
            }
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
}
