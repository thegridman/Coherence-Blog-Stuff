package com.thegridman.rail.movements.listener;

import com.tangosol.io.pof.ConfigurablePofContext;
import com.tangosol.net.CacheFactory;
import com.tangosol.net.ConfigurableCacheFactory;
import com.tangosol.net.InetAddressHelper;
import com.tangosol.util.Base;
import com.thegridman.rail.RailConstants;
import com.thegridman.rail.gson.RailGsonParser;
import com.thegridman.rail.kv.SchedulesDAO;
import com.thegridman.rail.movements.MovementsDAO;
import com.thegridman.rail.movements.TrainMovementActivationMessage;
import com.thegridman.rail.movements.TrainMovementMessage;
import com.thegridman.rail.movements.TrainMovementMessageType;
import com.thegridman.rail.schedules.Schedule;
import com.thegridman.rail.schedules.ScheduleId;
import com.thegridman.utils.cli.CommandLineArg;
import com.thegridman.utils.cli.CommandLineParser;
import net.ser1.stomp.Listener;
import oracle.kv.KVStore;
import oracle.kv.KVStoreConfig;
import oracle.kv.KVStoreFactory;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Jonathan Knight
 */
public class MovementsFeedHandler implements Listener
{
    private final SchedulesDAO schedulesDAO;
    private final MovementsDAO movementsDAO;
    private final RailGsonParser gson;
    private final AtomicBoolean running = new AtomicBoolean(false);

    public MovementsFeedHandler(SchedulesDAO schedulesDAO, MovementsDAO movementsDAO, RailGsonParser gson)
    {
        if (schedulesDAO == null)
        {
            throw new IllegalArgumentException("schedulesDAO cannot be null");
        }

        if (movementsDAO == null)
        {
            throw new IllegalArgumentException("movementsDAO cannot be null");
        }

        if (gson == null)
        {
            throw new IllegalArgumentException("gson cannot be null");
        }

        this.schedulesDAO = schedulesDAO;
        this.movementsDAO = movementsDAO;
        this.gson = gson;
    }

    @Override
    public void message(Map map, String line)
    {
        TrainMovementMessage message = gson.parseTrainMovementMessage(line);
        TrainMovementMessageType msgType = message.getHeader().getMsgType();
        if (msgType == TrainMovementMessageType.Activation)
        {
            processActivation((TrainMovementActivationMessage) message);
        }
        else
        {
            processMovement(message);
        }
    }

    private void processActivation(TrainMovementActivationMessage message)
    {
        String trainId = message.getId().getTrainId();
        String trainUid = message.getTrainUid();
        Schedule scheduleForTrainUID = schedulesDAO.getScheduleForTrainUID(trainUid, message.getTpOriginTimestamp());
        if (scheduleForTrainUID != null)
        {
            ScheduleId scheduleId = scheduleForTrainUID.getId();
            message.getId().setScheduleId(scheduleId);
            movementsDAO.setTrainScheduleMapping(trainId, scheduleId);
            movementsDAO.saveMessage(message);
        }
    }

    private void processMovement(TrainMovementMessage message)
    {
        String trainId = message.getId().getTrainId();
        ScheduleId scheduleId = movementsDAO.getScheduleIdForTrainId(trainId);
        if (scheduleId != null)
        {
            message.getId().setScheduleId(scheduleId);
            movementsDAO.saveMessage(message);
        }
    }

    public boolean isRunning()
    {
        return running.get();
    }

    public void start(MovementProvider provider)
    {
        try
        {
            provider.subscribe(this);
            running.set(true);
            while(running.get())
            {
                synchronized (this)
                {
                    this.wait(1000);
                }
            }
            provider.unsubscribe(this);
            provider.close();
        }
        catch (Exception e)
        {
            throw Base.ensureRuntimeException(e);
        }
    }

    public void startDaemon(final MovementProvider provider)
    {
        Thread thread = Base.makeThread(null, new Runnable()
        {
            @Override
            public void run()
            {
                MovementsFeedHandler.this.start(provider);
            }
        }, "MovementsFeedHandler");
        thread.setDaemon(true);
        thread.start();
    }

    public void stop()
    {
        synchronized (this)
        {
            running.set(false);
            this.notifyAll();
        }
    }

    public static void main(String[] args) throws Exception
    {
        MainArgs mainArgs = CommandLineParser.parse(new MainArgs(), args);
        String hostName = InetAddressHelper.getLocalHost().getHostName();
        String helperHosts = hostName + ":" + mainArgs.port;
        KVStore kvStore = KVStoreFactory.getStore(new KVStoreConfig(mainArgs.storeName, helperHosts));

        MovementProvider provider;
        if (mainArgs.simulationFileName != null && !mainArgs.simulationFileName.isEmpty())
        {
            provider = new MovementSimulator(mainArgs.simulationFileName);
        }
        else
        {
            provider = new NetworkRailMovementProvider();
        }


        ConfigurableCacheFactory cacheFactory = CacheFactory.getCacheFactoryBuilder().getConfigurableCacheFactory(null);
        MovementsDAO movementsDAO = new MovementsDAO(cacheFactory);
        ConfigurablePofContext pofContext = new ConfigurablePofContext(RailConstants.RAIL_POF_CONFIG_XML);
        MovementsFeedHandler feedHandler = new MovementsFeedHandler(new SchedulesDAO(kvStore, pofContext), movementsDAO, new RailGsonParser());
        feedHandler.startDaemon(provider);

        try
        {
            final Object waiter = new Object();
            if (mainArgs.runningTime == null || mainArgs.runningTime.isEmpty())
            {
                synchronized (waiter)
                {
                    waiter.wait();
                }
            }
            else
            {
                synchronized (waiter)
                {
                    waiter.wait(Base.parseTime(mainArgs.runningTime));
                }
            }
        }
        finally
        {
            feedHandler.stop();
        }
    }


    public static class MainArgs
    {
        @CommandLineArg(opt = "s", argName = "store-name", description = "The NoSQL store name", required = true)
        public String storeName;

        @CommandLineArg(opt = "p", argName = "port", description = "The NoSQL tcp port", required = true)
        public String port;

        @CommandLineArg(opt = "f", argName = "file-name", description = "The simulation file name", required = false)
        public String simulationFileName;

        @CommandLineArg(opt = "t", argName = "time", description = "The amount of time to run", required = false)
        public String runningTime;
    }
}
