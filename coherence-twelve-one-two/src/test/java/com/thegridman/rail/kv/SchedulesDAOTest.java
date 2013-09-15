package com.thegridman.rail.kv;

import com.tangosol.io.pof.ConfigurablePofContext;
import com.tangosol.io.pof.PofContext;
import com.tangosol.util.Binary;
import com.tangosol.util.ExternalizableHelper;
import com.thegridman.rail.RailConstants;
import com.thegridman.rail.schedules.Schedule;
import com.thegridman.rail.schedules.ScheduleId;
import com.thegridman.rail.schedules.enums.STPIndicator;
import oracle.kv.KVStore;
import oracle.kv.Key;
import oracle.kv.Value;
import oracle.kv.ValueVersion;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.Arrays;
import java.util.SortedSet;
import java.util.TreeSet;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Jonathan Knight
 */
public class SchedulesDAOTest
{
    private KVStore kvStore;
    private PofContext pofContext;
    private String trainUID = "testing";
    private Key key1;
    private Key key2;
    private Key key3;
    private Key key4;
    private Schedule schedule;
    private Binary binarySchedule;
    private ValueVersion versionValue;

    @Before
    public void setup()
    {
        pofContext = new ConfigurablePofContext(RailConstants.RAIL_POF_CONFIG_XML);
        kvStore = mock(KVStore.class);

        key1 = Key.createKey(Arrays.asList(SchedulesDAO.KEY_PREFIX_SCHEDULES, trainUID),
                             Arrays.asList("2013-1-10", "2013-7-21", STPIndicator.N.toString()));
        key2 = Key.createKey(Arrays.asList(SchedulesDAO.KEY_PREFIX_SCHEDULES, trainUID),
                             Arrays.asList("2013-1-10", "2013-7-21", STPIndicator.P.toString()));
        key3 = Key.createKey(Arrays.asList(SchedulesDAO.KEY_PREFIX_SCHEDULES, trainUID),
                             Arrays.asList("2013-7-22", "2013-8-22", STPIndicator.N.toString()));
        key4 = Key.createKey(Arrays.asList(SchedulesDAO.KEY_PREFIX_SCHEDULES, trainUID),
                             Arrays.asList("2013-7-22", "2013-8-22", STPIndicator.P.toString()));

        Interval interval = new Interval(DateTime.parse("2013-7-22"), DateTime.parse("2013-8-22"));
        schedule = new Schedule(new ScheduleId(trainUID, interval, STPIndicator.N));

        binarySchedule = ExternalizableHelper.toBinary(schedule, pofContext);
        Value value = Value.createValue(binarySchedule.toByteArray());
        versionValue = new ValueVersion(value, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfKVStoreConstructorArgIsNull() throws Exception
    {
        new SchedulesDAO(null, pofContext);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfPofContextConstructorArgIsNull() throws Exception
    {
        new SchedulesDAO(kvStore, null);
    }

    @Test
    public void shouldGetCorrectSchedule() throws Exception
    {
        SortedSet<Key> keys = new TreeSet<>(Arrays.asList(key1, key2, key3, key4));
        Key key = Key.createKey(Arrays.asList(SchedulesDAO.KEY_PREFIX_SCHEDULES, trainUID));

        when(kvStore.multiGetKeys(key, null, null)).thenReturn(keys);
        when(kvStore.get(key3)).thenReturn(versionValue);

        SchedulesDAO dao = new SchedulesDAO(kvStore, pofContext);
        Schedule result = dao.getScheduleForTrainUID(trainUID, "2013-7-22");
        assertThat(result, is(notNullValue()));
        assertThat(result.getId(), is(schedule.getId()));
    }

    @Test
    public void shouldReturnNullIfNoSchedulesMatchDate() throws Exception
    {
        SortedSet<Key> keys = new TreeSet<>(Arrays.asList(key1, key2, key3, key4));
        Key key = Key.createKey(Arrays.asList(SchedulesDAO.KEY_PREFIX_SCHEDULES, trainUID));

        when(kvStore.multiGetKeys(key, null, null)).thenReturn(keys);
        when(kvStore.get(key1)).thenReturn(versionValue);

        SchedulesDAO dao = new SchedulesDAO(kvStore, pofContext);
        Schedule result = dao.getScheduleForTrainUID(trainUID, "2014-7-22");
        assertThat(result, is(nullValue()));
    }

    @Test
    public void shouldSaveSchedule() throws Exception
    {
        SchedulesDAO dao = new SchedulesDAO(kvStore, pofContext);
        Key savedKey = dao.saveSchedule(schedule);

        assertThat(savedKey, is(key3));
        ArgumentCaptor<Value> valueArgumentCaptor = ArgumentCaptor.forClass(Value.class);
        verify(kvStore).put(eq(key3), valueArgumentCaptor.capture());
        Value value = valueArgumentCaptor.getValue();
        assertThat(value.getValue(), is(binarySchedule.toByteArray()));
    }

}
