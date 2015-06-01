package edu.hm.vss.helper;

import edu.hm.vss.server.RMIServer;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by B3rni on 27.05.2015.
 */
public class Logger implements Serializable
{
    private boolean doLog = true;
    private long startTime;

    public Logger()
    {
        startTime = new Date().getTime();
    }

    public void printLog(String from, String message)
    {
        DateFormat df = new SimpleDateFormat("mm:ss.SSS");
        Date date = new Date();
        date.setTime(new Date().getTime() - startTime);
        String timeString = df.format(date);
        System.out.println(timeString + " - " + from + " - " + message);
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
