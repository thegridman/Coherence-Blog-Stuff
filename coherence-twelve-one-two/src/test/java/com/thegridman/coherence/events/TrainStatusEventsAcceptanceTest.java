package com.thegridman.coherence.events;

import com.oracle.tools.deferred.DeferredAssert;
import com.tangosol.net.NamedCache;
import com.tangosol.net.cache.CacheEvent;
import com.tangosol.util.Filter;
import com.tangosol.util.MapEvent;
import com.tangosol.util.MapEventTransformer;
import com.tangosol.util.MapListener;
import com.tangosol.util.Resources;
import com.tangosol.util.extractor.PofExtractor;
import com.tangosol.util.extractor.ReflectionExtractor;
import com.tangosol.util.filter.EqualsFilter;
import com.tangosol.util.filter.MapEventFilter;
import com.tangosol.util.filter.MapEventTransformerFilter;
import com.tangosol.util.transformer.ExtractorEventTransformer;
import com.thegridman.rail.BaseRailAcceptanceTest;
import com.thegridman.rail.RailConstants;
import com.thegridman.rail.gson.RailGsonParser;
import com.thegridman.rail.kv.SchedulesDAO;
import com.thegridman.rail.movements.MovementsDAO;
import com.thegridman.rail.movements.TrainMovementMessage;
import com.thegridman.rail.movements.TrainStatus;
import com.thegridman.rail.movements.listener.MovementsFeedHandler;
import com.thegridman.rail.schedules.Schedule;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.oracle.tools.deferred.DeferredHelper.eventually;
import static com.oracle.tools.deferred.DeferredHelper.invoking;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author Jonathan Knight
 */
public class TrainStatusEventsAcceptanceTest extends BaseRailAcceptanceTest
{
    private MovementsFeedHandler movementsFeedHandler;
    private List<TrainMovementMessage> messages;
    private List<String> messageLines;

    private SchedulesDAO schedulesDAO;
    private MovementsDAO movementsDAO;

    private NamedCache schedulesCache;
    private NamedCache tiplocCache;
    private NamedCache movementsCache;
    private NamedCache statusCache;

    private MapListener listener;
    private MapEventCapturingListener capturedEvents;

    @BeforeClass
    public static void loadSchedules() throws Exception
    {
        loadScheduleFile("rail/data/schedules/schedule-P00299.gzip");
        loadScheduleFile("rail/data/schedules/schedule-P42315.gzip");
        loadScheduleFile("rail/data/schedules/schedule-P50098.gzip");
        loadScheduleFile("rail/data/schedules/schedule-P51202.gzip");
        loadScheduleFile("rail/data/schedules/schedule-W50384.gzip");
        loadScheduleFile("rail/data/schedules/schedule-Y41106.gzip");
        loadScheduleFile("rail/data/schedules/schedule-Y50124.gzip");
    }

    @Before
    public void setup() throws Exception
    {
        RailGsonParser gsonParser = new RailGsonParser();
        String movementsFile = "rail/data/multi-train.json";
        messages = gsonParser.loadAllTrainMovementMessage(movementsFile);
        messageLines = new ArrayList<>();
        URL url = Resources.findFileOrResource(movementsFile, null);
        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
        String line = reader.readLine();
        while(line != null)
        {
            messageLines.add(line);
            line = reader.readLine();
        }
        reader.close();

        schedulesDAO = new SchedulesDAO(store, pofContext);
        movementsDAO = new MovementsDAO(clientCacheFactory);

        movementsFeedHandler = new MovementsFeedHandler(schedulesDAO, movementsDAO, gsonParser);

        tiplocCache = getCache(RailConstants.CACHENAME_TIPLOC);
        schedulesCache = getCache(RailConstants.CACHENAME_TRAIN_SCHEDULES);
        movementsCache = getCache(RailConstants.CACHENAME_TRAIN_MOVEMENTS);
        statusCache = getCache(RailConstants.CACHENAME_TRAIN_STATUS);

        if (tiplocCache.isEmpty())
        {
            loadTiplocCache("rail/data/schedules/tiploc-cutdown-data.gzip");
        }

        movementsCache.clear();
        statusCache.clear();

        capturedEvents = new MapEventCapturingListener();
        listener = new MultiMapListener(
            new PrintingMapListener(),
            capturedEvents
        );
    }

    @Test
    public void wholeCacheListener() throws Exception
    {
        System.err.println("In Test wholeCacheListener --------------------------------");
        statusCache.addMapListener(listener);

        for (String line : messageLines)
        {
            movementsFeedHandler.message(Collections.emptyMap(), line);
        }

        DeferredAssert.assertThat(eventually(invoking(capturedEvents).size()), is(194));
        statusCache.removeMapListener(listener);
    }

    @Test
    public void singleKeyMapListener() throws Exception
    {
        System.err.println("In Test singleKeyMapListener --------------------------------");

        Schedule schedule = schedulesDAO.getScheduleForTrainUID("Y41106", "2013-08-02");
        String trainId = "162C37MO02";
        TrainStatus.Id id = new TrainStatus.Id(trainId, schedule.getId());

        statusCache.addMapListener(listener, id, false);

        for (String line : messageLines)
        {
            movementsFeedHandler.message(Collections.emptyMap(), line);
        }

        statusCache.removeMapListener(listener, id);
    }

    @Test
    public void filterMapListenerForInserts() throws Exception
    {
        System.err.println("In Test filterMapListenerForInserts --------------------------------");

        MapEventFilter mapEventFilter = new MapEventFilter(MapEventFilter.E_INSERTED);

        statusCache.addMapListener(listener, mapEventFilter, false);

        for (String line : messageLines)
        {
            movementsFeedHandler.message(Collections.emptyMap(), line);
        }

        statusCache.removeMapListener(listener, mapEventFilter);
    }

    /**
     * Display all events where the status changes from Delayed to something else
     * or to Delayed from something else.
     *
     * @throws Exception
     */
    @Test
    public void filterMapListener() throws Exception
    {
        System.err.println("In Test filterMapListener --------------------------------");

        Filter filter = new EqualsFilter(new PofExtractor(null, TrainStatus.POF_STATUS), TrainStatus.Status.Delayed);
        MapEventFilter mapEventFilter = new MapEventFilter(filter);

        statusCache.addMapListener(listener, mapEventFilter, false);

        for (String line : messageLines)
        {
            movementsFeedHandler.message(Collections.emptyMap(), line);
        }

        statusCache.removeMapListener(listener, mapEventFilter);
    }

    @Test
    public void shouldTransformUsingExtractor() throws Exception
    {
        System.err.println("In Test shouldTransformUsingExtractor --------------------------------");

        Filter filter = new EqualsFilter(new PofExtractor(null, TrainStatus.POF_STATUS), TrainStatus.Status.Delayed);
        MapEventFilter mapEventFilter = new MapEventFilter(filter);
        MapEventTransformer transformer = new ExtractorEventTransformer(new ReflectionExtractor("getStatus"));
        MapEventTransformerFilter transformerFilter = new MapEventTransformerFilter(mapEventFilter, transformer);

        statusCache.addMapListener(listener, transformerFilter, false);

        for (String line : messageLines)
        {
            movementsFeedHandler.message(Collections.emptyMap(), line);
        }

        statusCache.removeMapListener(listener, transformerFilter);
    }

    @Test
    public void shouldTransformUsingPofExtractor() throws Exception
    {
        System.err.println("In Test shouldTransformUsingPofExtractor --------------------------------");

        Filter filter = new EqualsFilter(new PofExtractor(null, TrainStatus.POF_STATUS), TrainStatus.Status.Delayed);
        MapEventFilter mapEventFilter = new MapEventFilter(filter);
        MapEventTransformer transformer = new PofExtractorEventTransformer(new PofExtractor(null, TrainStatus.POF_STATUS));
        MapEventTransformerFilter transformerFilter = new MapEventTransformerFilter(mapEventFilter, transformer);

        statusCache.addMapListener(listener, transformerFilter, false);

        for (String line : messageLines)
        {
            movementsFeedHandler.message(Collections.emptyMap(), line);
        }

        statusCache.removeMapListener(listener, transformerFilter);
    }

    @Test
    public void shouldTransform() throws Exception
    {
        System.err.println("In Test shouldTransform --------------------------------");

        Filter filter = new EqualsFilter(new PofExtractor(null, TrainStatus.POF_STATUS), TrainStatus.Status.Delayed);
        MapEventFilter mapEventFilter = new MapEventFilter(filter);
        MapEventTransformer transformer = new TrainStatusTransformer();
        MapEventTransformerFilter transformerFilter = new MapEventTransformerFilter(mapEventFilter, transformer);

        statusCache.addMapListener(listener, transformerFilter, false);

        for (String line : messageLines)
        {
            movementsFeedHandler.message(Collections.emptyMap(), line);
        }

        statusCache.removeMapListener(listener, transformerFilter);
    }

    @Test
    public void shouldGetSyntheticEvents() throws Exception
    {
        System.err.println("In Test shouldGetSyntheticEvents --------------------------------");
        statusCache.addMapListener(listener);

        Schedule schedule = schedulesDAO.getScheduleForTrainUID("Y41106", "2013-08-02");
        String trainId = "162C37MO02";
        TrainStatus.Id id = new TrainStatus.Id(trainId, schedule.getId());
        TrainStatus status = new TrainStatus(id);
        statusCache.put(id, status, 3L);
        for (int i=0; i<4; i++)
        {
            Thread.sleep(1000L);
            statusCache.get(id);
        }

        statusCache.removeMapListener(listener);

        MapEvent lastEvent = capturedEvents.getLast();
        assertThat(((CacheEvent)lastEvent).isSynthetic(), is(true));
    }

}
