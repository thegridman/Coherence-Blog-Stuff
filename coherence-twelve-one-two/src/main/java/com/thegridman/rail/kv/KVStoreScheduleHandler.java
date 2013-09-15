package com.thegridman.rail.kv;

import com.tangosol.io.pof.ConfigurablePofContext;
import com.tangosol.io.pof.PofContext;
import com.tangosol.net.CacheFactory;
import com.tangosol.net.InetAddressHelper;
import com.tangosol.util.Resources;
import com.thegridman.rail.RailConstants;
import com.thegridman.rail.gson.RailGsonParser;
import com.thegridman.rail.schedules.Schedule;
import com.thegridman.rail.schedules.ScheduleLoader;
import com.thegridman.utils.cli.CommandLineArg;
import com.thegridman.utils.cli.CommandLineParser;
import oracle.kv.KVStore;
import oracle.kv.KVStoreConfig;
import oracle.kv.KVStoreFactory;
import oracle.kv.Key;

import java.net.URL;

/**
 * @author Jonathan Knight
 */
public class KVStoreScheduleHandler extends ScheduleLoader.DefaultHandler implements RailConstants
{
    private final SchedulesDAO schedulesDAO;

    public KVStoreScheduleHandler(KVStore kvStore, PofContext pofContext)
    {
        this(new SchedulesDAO(kvStore, pofContext));
    }

    public KVStoreScheduleHandler(SchedulesDAO schedulesDAO)
    {
        this.schedulesDAO = schedulesDAO;
    }

    @Override
    public void handleSchedule(Schedule schedule, String json)
    {
        Key key = schedulesDAO.saveSchedule(schedule);
        CacheFactory.log(String.format("Stored schedule id=[%s] with key=[%s]", schedule, key), CacheFactory.LOG_DEBUG);
    }

    public static void main(String[] args) throws Exception
    {
        MainArgs mainArgs = CommandLineParser.parse(new MainArgs(), args);

        String hostName = InetAddressHelper.getLocalHost().getHostName();
        String helperHosts = hostName + ":" + mainArgs.port;

        KVStore kvStore = KVStoreFactory.getStore(new KVStoreConfig(mainArgs.storeName, helperHosts));
        ScheduleLoader.Handler handler = new KVStoreScheduleHandler(kvStore, new ConfigurablePofContext(RAIL_POF_CONFIG_XML));

        URL url = Resources.findFileOrResource(mainArgs.scheduleFileName, null);
        ScheduleLoader loader = new ScheduleLoader(url, new RailGsonParser());

        loader.load(handler);
    }

    public static class MainArgs
    {
        @CommandLineArg(opt = "f", description = "The schedule file name", required = true)
        public String scheduleFileName;

        @CommandLineArg(opt = "s", description = "The NoSQL store name", required = true)
        public String storeName;

        @CommandLineArg(opt = "p", description = "The NoSQL tcp port", required = true)
        public String port;
    }
}
