package com.thegridman.rail.schedules;

import com.tangosol.util.Resources;
import com.thegridman.rail.gson.RailGsonParser;
import com.thegridman.utils.cli.CommandLineArg;
import com.thegridman.utils.cli.CommandLineParser;
import org.apache.commons.cli.Option;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPOutputStream;

/**
 * @author Jonathan Knight
 */
public class FilteringScheduleLoaderHandler extends ScheduleLoader.DefaultHandler
{
    private final String trainUID;
    private final String fileName;
    private PrintWriter writer;

    public FilteringScheduleLoaderHandler(String trainUID, String fileName)
    {
        this.trainUID = trainUID;
        this.fileName = fileName;
    }

    @Override
    public void handleHeaderLine(String header) throws IOException
    {
        GZIPOutputStream stream = new GZIPOutputStream(new FileOutputStream(fileName));
        writer = new PrintWriter(new OutputStreamWriter(stream));
        writer.println(header);
        writer.flush();
    }

    @Override
    public void handleSchedule(Schedule schedule, String json)
    {
        if (trainUID.equals(schedule.getId().getTrainUID()))
        {
            writer.println(json);
            writer.flush();
        }
    }

    @Override
    public void handleTrailerLine(String trailer)
    {
        writer.println(trailer);
        writer.flush();
        writer.close();
    }

    public static void main(String[] args) throws Exception
    {
        MainArgs mainArgs = CommandLineParser.parse(new MainArgs(), args);
        URL url = Resources.findFileOrResource(mainArgs.fileName, null);
        RailGsonParser parser = new RailGsonParser();

        String schedulesDirectory = mainArgs.destination;
        if (!schedulesDirectory.endsWith(File.separator))
        {
            schedulesDirectory = schedulesDirectory + File.separatorChar;
        }

        List<ScheduleLoader.Handler> handlers = new ArrayList<>();
        handlers.add(new LoggingScheduleLoaderHandler(System.out));

        for (String uid : mainArgs.uidList)
        {
            handlers.add(new FilteringScheduleLoaderHandler(uid, schedulesDirectory + "schedule-" + uid + ".gzip"));
        }

        ScheduleLoader.Handler handler = new MultiplexingScheduleLoaderHandler(handlers);

        ScheduleLoader loader = new ScheduleLoader(url, parser);
        loader.load(handler);
    }

    public static class MainArgs
    {
        @CommandLineArg(opt = "f", description = "The schedule gzip file name", required = true)
        public String fileName;

        @CommandLineArg(opt = "d", description = "The destination directory", required = true)
        public String destination;

        @CommandLineArg(opt = "u", description = "The comma-delimited list of train UID values", required = true, numberOfArgs = Option.UNLIMITED_VALUES)
        public String[] uidList;
    }

}
