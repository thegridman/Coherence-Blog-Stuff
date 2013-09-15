package com.thegridman.rail.movements;

import com.tangosol.net.NamedCache;
import com.thegridman.rail.BaseRailAcceptanceTest;
import com.thegridman.rail.RailConstants;
import com.thegridman.rail.gson.RailGsonParser;
import com.thegridman.rail.kv.SchedulesDAO;
import com.thegridman.rail.schedules.Schedule;
import com.thegridman.rail.schedules.ScheduleId;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * @author Jonathan Knight
 */
public class TrainStatusUpdateInterceptorAcceptanceTest extends BaseRailAcceptanceTest
{
    private String scheduleTrainUID;
    private String trainId;
    private TrainMovementActivationMessage activationMessage;
    private Schedule schedule;
    private ScheduleId scheduleId;
    private TrainStatus.Id statusId;
    private List<TrainMovementMessage> messages;

    private SchedulesDAO schedulesDAO;
    private MovementsDAO movementsDAO;

    private NamedCache movementsCache;
    private NamedCache statusCache;

    @BeforeClass
    public static void loadSchedules() throws Exception
    {
        loadScheduleFile("rail/data/schedules/schedule-P51202.gzip");
    }

    @Before
    public void setup() throws Exception
    {
        RailGsonParser parser = new RailGsonParser();
        messages = parser.loadAllTrainMovementMessage("rail/data/train-721H69MP02.json");
        activationMessage = (TrainMovementActivationMessage) messages.get(0);
        trainId = activationMessage.getId().getTrainId();
        scheduleTrainUID = activationMessage.getTrainUid();

        schedulesDAO = new SchedulesDAO(store, pofContext);
        schedule = schedulesDAO.getScheduleForTrainUID(scheduleTrainUID, activationMessage.getTpOriginTimestamp());
        scheduleId = schedule.getId();

        for (TrainMovementMessage message : messages)
        {
            message.getId().setScheduleId(scheduleId);
        }

        statusId = new TrainStatus.Id(trainId, scheduleId);

        movementsDAO = new MovementsDAO(clientCacheFactory);
        movementsDAO.setTrainScheduleMapping(trainId, scheduleId);

        movementsCache = getCache(RailConstants.CACHENAME_TRAIN_MOVEMENTS);
        statusCache = getCache(RailConstants.CACHENAME_TRAIN_STATUS);

        movementsCache.clear();
        statusCache.clear();
    }

    @Test
    public void shouldHandleActivation() throws Exception
    {
        movementsCache.put(activationMessage.getId(), activationMessage);
        TrainStatus status = (TrainStatus) statusCache.get(statusId);
        assertThat(status, is(notNullValue()));
        assertThat(status.getId(), is(statusId));
        assertThat(status.getStatus(), is(TrainStatus.Status.Activated));
        assertThat(status.getLastMessageId(), is(activationMessage.getId()));
    }

    @Test
    public void shouldHandleFirstMovement() throws Exception
    {
        movementsCache.put(activationMessage.getId(), activationMessage);
        TrainMovementMessage message = messages.get(1);
        movementsCache.put(message.getId(), message);
        TrainStatus status = (TrainStatus) statusCache.get(statusId);
        assertThat(status, is(notNullValue()));
        assertThat(status.getId(), is(statusId));
        assertThat(status.getStatus(), is(TrainStatus.Status.OnTime));
        assertThat(status.getTimeDifference(), is(0L));
        assertThat(status.getLastMessageId(), is(message.getId()));
    }

}
