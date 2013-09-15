package com.thegridman.rail.schedules;

import com.tangosol.util.Resources;
import com.thegridman.rail.gson.RailGsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.zip.GZIPInputStream;

/**
 * @author Jonathan Knight
 */
public class ScheduleLoader
{

    private URL gzipURL;
    private final RailGsonParser parser;

    public ScheduleLoader(String gzipFileName, RailGsonParser parser)
    {
        this(Resources.findFileOrResource(gzipFileName, null), parser);
    }

    public ScheduleLoader(URL gzipURL, RailGsonParser parser)
    {
        this.gzipURL = gzipURL;
        this.parser = parser;
    }

    public void load(Handler handler) throws IOException
    {
        GZIPInputStream inputStream = new GZIPInputStream(gzipURL.openStream());
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream)))
        {
            String line = reader.readLine();
            while (line != null)
            {
                ScheduleEntry entry = parser.parseScheduleEntry(line);
                if (entry instanceof Schedule)
                {
                    handler.handleSchedule((Schedule) entry, line);
                }
                else if (entry instanceof ScheduleAssociation)
                {
                    handler.handleAssociation((ScheduleAssociation) entry, line);
                }
                else if (entry instanceof Tiploc)
                {
                    handler.handleTiploc((Tiploc) entry, line);
                }
                else if (line.startsWith("{\"JsonTimetableV1\":"))
                {
                    handler.handleHeaderLine(line);
                }
                else if (line.equals("{\"EOF\":true}"))
                {
                    handler.handleTrailerLine(line);
                }
                else
                {
                    System.out.println("Unhandled line: " + line);
                }
                line = reader.readLine();
            }
        }
    }

    public static interface Handler
    {
        void handleHeaderLine(String header) throws IOException;
        void handleAssociation(ScheduleAssociation association, String json) throws IOException;
        void handleTiploc(Tiploc tiploc, String json) throws IOException;
        void handleSchedule(Schedule schedule, String json) throws IOException;
        void handleTrailerLine(String trailer) throws IOException;
    }

    public static class DefaultHandler implements Handler
    {
        @Override
        public void handleHeaderLine(String header) throws IOException
        {
        }

        @Override
        public void handleAssociation(ScheduleAssociation association, String json) throws IOException
        {
        }

        @Override
        public void handleTiploc(Tiploc tiploc, String json) throws IOException
        {
        }

        @Override
        public void handleSchedule(Schedule schedule, String json) throws IOException
        {
        }

        @Override
        public void handleTrailerLine(String trailer) throws IOException
        {
        }
    }
}
