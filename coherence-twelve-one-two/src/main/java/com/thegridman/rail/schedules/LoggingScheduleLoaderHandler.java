package com.thegridman.rail.schedules;

import java.io.IOException;
import java.io.PrintStream;

/**
 * A simple implementation of a {@link ScheduleLoader.Handler} that
 * prints the parsed objects to the specified {@link PrintStream}.
 *
 * @author Jonathan Knight
 */
public class LoggingScheduleLoaderHandler implements ScheduleLoader.Handler
{
    private final PrintStream out;

    public LoggingScheduleLoaderHandler(PrintStream out)
    {
        this.out = out;
    }

    @Override

    public void handleAssociation(ScheduleAssociation association, String json) throws IOException
    {
        out.println(association);
    }

    @Override
    public void handleHeaderLine(String header) throws IOException
    {
        out.println(header);
    }

    @Override
    public void handleTiploc(Tiploc tiploc, String json) throws IOException
    {
        out.println(tiploc);
    }

    @Override
    public void handleSchedule(Schedule schedule, String json) throws IOException
    {
        out.println(schedule);
    }

    @Override
    public void handleTrailerLine(String trailer) throws IOException
    {
        out.println(trailer);
    }
}
