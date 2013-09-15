package com.thegridman.rail.schedules;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author Jonathan Knight
 */
public class MultiplexingScheduleLoaderHandler implements ScheduleLoader.Handler
{
    private final List<ScheduleLoader.Handler> handlers;

    public MultiplexingScheduleLoaderHandler(ScheduleLoader.Handler... handlers)
    {
        this.handlers = Arrays.asList(handlers);
    }

    public MultiplexingScheduleLoaderHandler(Collection<ScheduleLoader.Handler> handlers)
    {
        this.handlers = new ArrayList<>();
        this.handlers.addAll(handlers);
    }

    @Override

    public void handleAssociation(ScheduleAssociation association, String json) throws IOException
    {
        for (ScheduleLoader.Handler handler : handlers)
        {
            handler.handleAssociation(association, json);
        }
    }

    @Override
    public void handleHeaderLine(String header) throws IOException
    {
        for (ScheduleLoader.Handler handler : handlers)
        {
            handler.handleHeaderLine(header);
        }
    }

    @Override
    public void handleTiploc(Tiploc tiploc, String json) throws IOException
    {
        for (ScheduleLoader.Handler handler : handlers)
        {
            handler.handleTiploc(tiploc, json);
        }
    }

    @Override
    public void handleSchedule(Schedule schedule, String json) throws IOException
    {
        for (ScheduleLoader.Handler handler : handlers)
        {
            handler.handleSchedule(schedule, json);
        }
    }

    @Override
    public void handleTrailerLine(String trailer) throws IOException
    {
        for (ScheduleLoader.Handler handler : handlers)
        {
            handler.handleTrailerLine(trailer);
        }
    }
}
