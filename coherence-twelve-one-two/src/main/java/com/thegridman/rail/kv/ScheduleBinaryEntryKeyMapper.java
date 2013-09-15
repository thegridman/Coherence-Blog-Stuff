package com.thegridman.rail.kv;

import com.tangosol.util.BinaryEntry;
import com.thegridman.rail.schedules.ScheduleId;
import oracle.kv.Key;
import oracle.kv.coherence.BinaryEntryKeyMapper;
import org.joda.time.DateTime;

import java.util.Arrays;
import java.util.List;

/**
 * @author Jonathan Knight
 */
public class ScheduleBinaryEntryKeyMapper implements BinaryEntryKeyMapper
{
    public static final String DATE_FORMAT = "%1$04d-%2$02d-%3$02d";

    public ScheduleBinaryEntryKeyMapper()
    {
    }

    @Override
    public Key mapCacheKey(BinaryEntry entry)
    {
        return mapCacheKey(entry.getKey());
    }

    @Override
    public Key mapCacheKey(Object key)
    {
        ScheduleId id = (ScheduleId) key;
        return mapCacheKey(id.getTrainUID(),
                           id.getStartDate(),
                           id.getEndDate(),
                           id.getStpIndicator().toString());
    }

    public Key mapCacheKey(String trainUID, DateTime startDate, DateTime endDate, String stpIndicator)
    {
        List<String> majorParts = Arrays.asList(SchedulesDAO.KEY_PREFIX_SCHEDULES, trainUID);
        String dateFrom = String.format(DATE_FORMAT,
                                        startDate.year().get(),
                                        startDate.monthOfYear().get(),
                                        startDate.dayOfMonth().get());
        String dateTo = String.format(DATE_FORMAT,
                                      endDate.year().get(),
                                      endDate.monthOfYear().get(),
                                      endDate.dayOfMonth().get());
        List<String> minorParts = Arrays.asList(dateFrom, dateTo, stpIndicator);
        return Key.createKey(majorParts, minorParts);
    }
}
