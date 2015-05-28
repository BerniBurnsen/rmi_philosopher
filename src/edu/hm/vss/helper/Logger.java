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
    private final boolean doLog = true;

    public Logger()
    {

    }

    public void printLog(String from, String message)
    {
        DateFormat df = new SimpleDateFormat("mm:ss.SSS");
        String timeString = df.format(new Date());
        System.out.println(timeString + " - " + from + " - " + message);
    }
}
