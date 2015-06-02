package edu.hm.vss.helper;

import edu.hm.vss.server.RMIServer;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by B3rni on 27.05.2015.
 */
public class Logger implements Serializable
{
    private boolean doLog = true;
    private long startTime;
    private List<LogLevel> levels = new ArrayList<>();

    public Logger(LogLevel... levels)
    {
        startTime = new Date().getTime();
        for(LogLevel l : levels)
        {
            this.levels.add(l);
        }
    }

    public void printLog(LogLevel level, String from, String message)
    {
        DateFormat df = new SimpleDateFormat("mm:ss.SSS");
        Date date = new Date();
        date.setTime(new Date().getTime() - startTime);
        String timeString = df.format(date);
        if(levels.contains(level))
        {
            System.out.println(level + ":\t " + timeString + " - " + from + " - " + message);
        }
    }

    public boolean isDoLog()
    {
        return doLog;
    }

    public void setDoLog(boolean doLog)
    {
        this.doLog = doLog;
    }
}
