package edu.hm.vss.helper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Created by B3rni on 27.05.2015.
 */
public class Logger
{
    private final boolean doLog = true;
    private static Logger logger;

    private Logger()
    {

    }

    public void printLog(String from, String message)
    {
        DateFormat df = new SimpleDateFormat("mm:ss.SSSXXX");
        String timeString = df.format(DateFormat.getInstance());
        System.out.println(timeString + " - " +from+" - " + message);
    }

    public static Logger getInstance()
    {
        if(logger == null)
        {
            logger = new Logger();

        }
        return logger;
    }
}
