package edu.hm.vss.helper;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Logging System.
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
            System.out.println(level + (level.toString().length() < 8 ? "\t" : "") + timeString + " - " + from + " - " + message);
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
