package com.thegridman.rail.movements.listener;

import com.oracle.tools.deferred.DeferredAssert;
import com.thegridman.rail.gson.RailGsonParser;
import com.thegridman.rail.kv.SchedulesDAO;
import com.thegridman.rail.movements.*;
import com.thegridman.rail.schedules.Schedule;
import com.thegridman.rail.schedules.ScheduleId;
import com.thegridman.rail.schedules.enums.STPIndicator;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

import java.util.Collections;

import static com.oracle.tools.deferred.DeferredHelper.eventually;
import static com.oracle.tools.deferred.DeferredHelper.invoking;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Jonathan Knight
 */
public class MovementsFeedHandlerTest
{
    private SchedulesDAO schedulesDAO;
    private MovementsDAO movementsDAO;
    private RailGsonParser gson;
    private Schedule schedule;
    private String messageText = "testMessage";
    private String timestamp = "2013-07-22";
    private String trainUID = "train-uid";
    private String trainId = "test-train";

    @Before
    public void setup()
    {
        schedulesDAO = mock(SchedulesDAO.class);
        movementsDAO = mock(MovementsDAO.class);
        gson = mock(RailGsonParser.class);

        DateTime start = DateTime.parse("2013-01-10").withTimeAtStartOfDay();
        DateTime end = DateTime.parse("2013-07-22").withTimeAtStartOfDay();
        ScheduleId id = new ScheduleId("test", new Interval(start, end), STPIndicator.N);
        schedule = new Schedule(id);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfSchedulesDAOIsNull() throws Exception
    {
        new MovementsFeedHandler(null, movementsDAO, gson);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfMovementsDAOIsNull() throws Exception
    {
        new MovementsFeedHandler(schedulesDAO, null, gson);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfGsonIsNull() throws Exception
    {
        new MovementsFeedHandler(schedulesDAO, movementsDAO, null);
    }

    @Test
    public void shouldRunCorrectly() throws Exception
    {
        MovementProvider provider = mock(MovementProvider.class);

        MovementsFeedHandler handler = new MovementsFeedHandler(schedulesDAO, movementsDAO, gson);
        handler.startDaemon(provider);

        DeferredAssert.assertThat(eventually(invoking(handler).isRunning()), is(true));

        handler.stop();

        DeferredAssert.assertThat(eventually(invoking(handler).isRunning()), is(false));

        InOrder inOrder = inOrder(provider);
        inOrder.verify(provider).subscribe(same(handler));
        inOrder.verify(provider).unsubscribe(same(handler));
        inOrder.verify(provider).close();
    }

    @Test
    public void shouldHandleTrainMovementActivationMessage() throws Exception
    {
        TrainMovementActivationMessage activationMessage = new TrainMovementActivationMessage(trainId);
        activationMessage.setTrainUID(trainUID);
        activationMessage.setTpOriginTimestamp(timestamp);

        when(gson.parseTrainMovementMessage(messageText)).thenReturn(activationMessage);
        when(schedulesDAO.getScheduleForTrainUID(trainUID, timestamp)).thenReturn(schedule);

        MovementsFeedHandler handler = new MovementsFeedHandler(schedulesDAO, movementsDAO, gson);
        handler.message(Collections.emptyMap(), messageText);

        assertThat(activationMessage.getId().getScheduleId(), is(schedule.getId()));
        verify(movementsDAO).setTrainScheduleMapping(trainId, schedule.getId());
        verify(movementsDAO).saveMessage(same(activationMessage));
    }

    @Test
    public void shouldHandleTrainMovementCancellationMessage() throws Exception
    {
        shouldHandleOtherMessages(new TrainMovementCancellationMessage(trainId));
    }

    @Test
    public void shouldHandleTrainMovementChangeOfIdentityMessage() throws Exception
    {
        shouldHandleOtherMessages(new TrainMovementChangeOfIdentityMessage(trainId));
    }

    @Test
    public void shouldHandleTrainMovementChangeOfLocationMessage() throws Exception
    {
        shouldHandleOtherMessages(new TrainMovementChangeOfLocationMessage(trainId));
    }

    @Test
    public void shouldHandleTrainMovementChangeOfOriginMessage() throws Exception
    {
        shouldHandleOtherMessages(new TrainMovementChangeOfOriginMessage(trainId));
    }

    @Test
    public void shouldHandleTrainMovementMovementMessage() throws Exception
    {
        shouldHandleOtherMessages(new TrainMovementMovementMessage(trainId));
    }

    @Test
    public void shouldHandleTrainMovementReinstatementMessage() throws Exception
    {
        shouldHandleOtherMessages(new TrainMovementReinstatementMessage(trainId));
    }

    @Test
    public void shouldHandleTrainMovementUnidentifiedTrainMessage() throws Exception
    {
        shouldHandleOtherMessages(new TrainMovementUnidentifiedTrainMessage(trainId));
    }


    private void shouldHandleOtherMessages(TrainMovementMessage message) throws Exception
    {
        when(gson.parseTrainMovementMessage(messageText)).thenReturn(message);
        when(movementsDAO.getScheduleIdForTrainId(trainId)).thenReturn(schedule.getId());

        MovementsFeedHandler handler = new MovementsFeedHandler(schedulesDAO, movementsDAO, gson);
        handler.message(Collections.emptyMap(), messageText);

        assertThat(message.getId().getScheduleId(), is(schedule.getId()));
        verify(movementsDAO).saveMessage(same(message));
    }


}
