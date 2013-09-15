package com.thegridman.rail.kv;

import com.tangosol.util.BinaryEntry;
import com.thegridman.rail.schedules.ScheduleId;
import com.thegridman.rail.schedules.enums.STPIndicator;
import oracle.kv.Key;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Jonathan Knight
 */
public class ScheduleBinaryEntryKeyMapperTest
{

    @Test
    public void shouldConvertScheduleIdToKey() throws Exception
    {
        DateTime start = DateTime.parse("2013-01-08").withTimeAtStartOfDay();
        DateTime end = DateTime.parse("2013-07-22").withTimeAtStartOfDay();
        ScheduleId id = new ScheduleId("test", new Interval(start, end), STPIndicator.N);
        Key expected = Key.createKey(Arrays.asList(SchedulesDAO.KEY_PREFIX_SCHEDULES, "test"),
                                     Arrays.asList("2013-01-08", "2013-07-22", STPIndicator.N.toString()));

        ScheduleBinaryEntryKeyMapper mapper = new ScheduleBinaryEntryKeyMapper();
        assertThat(mapper.mapCacheKey(id), is(expected));
    }

    @Test
    public void shouldMapEntryToKey() throws Exception
    {
        DateTime start = DateTime.parse("2013-01-08").withTimeAtStartOfDay();
        DateTime end = DateTime.parse("2013-07-22").withTimeAtStartOfDay();
        ScheduleId id = new ScheduleId("test", new Interval(start, end), STPIndicator.N);

        Key expected = Key.createKey(Arrays.asList(SchedulesDAO.KEY_PREFIX_SCHEDULES, "test"),
                                     Arrays.asList("2013-01-08", "2013-07-22", STPIndicator.N.toString()));

        BinaryEntry entry = mock(BinaryEntry.class);
        when(entry.getKey()).thenReturn(id);

        ScheduleBinaryEntryKeyMapper mapper = new ScheduleBinaryEntryKeyMapper();
        assertThat(mapper.mapCacheKey(entry), is(expected));
    }

}
