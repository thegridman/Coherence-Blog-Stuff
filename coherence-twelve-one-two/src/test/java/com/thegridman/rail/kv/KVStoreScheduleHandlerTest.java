package com.thegridman.rail.kv;

import com.thegridman.rail.schedules.Schedule;
import com.thegridman.rail.schedules.ScheduleId;
import com.thegridman.rail.schedules.enums.STPIndicator;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * @author Jonathan Knight
 */
public class KVStoreScheduleHandlerTest
{
    private SchedulesDAO schedulesDAO;
    private String trainUID = "testing";
    private Schedule schedule;
    private ScheduleId scheduleId;

    @Before
    public void setup()
    {
        schedulesDAO = mock(SchedulesDAO.class);
        Interval interval = new Interval(DateTime.parse("2013-7-22"), DateTime.parse("2013-8-22"));
        scheduleId = new ScheduleId(trainUID, interval, STPIndicator.N);
        schedule = new Schedule(scheduleId);
    }

    @Test
    public void shouldSaveSchedule() throws Exception
    {
        KVStoreScheduleHandler handler = new KVStoreScheduleHandler(schedulesDAO);
        handler.handleSchedule(schedule, null);
        verify(schedulesDAO).saveSchedule(schedule);
    }

}
