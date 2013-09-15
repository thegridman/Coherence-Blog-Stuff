package com.thegridman.rail.kv;

import com.tangosol.io.ByteArrayReadBuffer;
import com.tangosol.io.pof.PofContext;
import com.tangosol.io.pof.reflect.PofValue;
import com.tangosol.io.pof.reflect.PofValueParser;
import com.tangosol.util.Binary;
import com.tangosol.util.ExternalizableHelper;
import com.thegridman.rail.schedules.Schedule;
import com.thegridman.rail.schedules.ScheduleId;
import com.thegridman.rail.schedules.enums.STPIndicator;
import oracle.kv.KVStore;
import oracle.kv.Key;
import oracle.kv.Value;
import oracle.kv.ValueVersion;
import org.joda.time.DateTime;
import org.joda.time.Interval;

import java.util.Arrays;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeMap;

/**
 * @author Jonathan Knight
 */
public class SchedulesDAO
{
    public static final String KEY_PREFIX_SCHEDULES = "schedules";
    public static final String KEY_PREFIX_TRAINUID = "trainuid";

    private final KVStore kvStore;
    private final PofContext pofContext;
    private final ScheduleBinaryEntryKeyMapper keyMapper;

    public SchedulesDAO(KVStore kvStore, PofContext pofContext)
    {
        if (kvStore == null)
        {
            throw new IllegalArgumentException("kvStore parameter cannot be null");
        }

        if (pofContext == null)
        {
            throw new IllegalArgumentException("pofContext parameter cannot be null");
        }

        this.kvStore = kvStore;
        this.pofContext = pofContext;
        this.keyMapper = new ScheduleBinaryEntryKeyMapper();
    }

    public Schedule getScheduleForTrainUID(String trainUID, String time)
    {
        Key key = Key.createKey(Arrays.asList(KEY_PREFIX_SCHEDULES, trainUID));
        SortedSet<Key> keys = kvStore.multiGetKeys(key, null, null);
        TreeMap<ScheduleId,Key> idSet = new TreeMap<>();
        for (Key next : keys)
        {
            List<String> minorParts = next.getMinorPath();

            String dateFrom = minorParts.get(0);
            if (dateFrom.compareTo(time) <= 0)
            {
                String dateTo = minorParts.get(1);
                if (dateTo.compareTo(time) >= 0)
                {
                    ScheduleId id = new ScheduleId(trainUID,
                                                   new Interval(DateTime.parse(dateFrom), DateTime.parse(dateTo)),
                                                   STPIndicator.fromDescription(minorParts.get(2)));
                    idSet.put(id, next);
                }
            }
        }

        if (idSet.isEmpty())
        {
            return null;
        }

        Key scheduleKey = idSet.get(idSet.firstKey());
        ValueVersion valueVersion = kvStore.get(scheduleKey);
        byte[] bytes = valueVersion.getValue().getValue();
        PofValue pofValue = PofValueParser.parse(new ByteArrayReadBuffer(bytes), pofContext);
        return (Schedule) pofValue.getValue();
    }

    public Key saveSchedule(Schedule schedule)
    {
        Binary binarySchedule = ExternalizableHelper.toBinary(schedule, pofContext);
        return saveSchedule(schedule.getId(), binarySchedule);
    }

    public Key saveSchedule(ScheduleId id, Binary binarySchedule)
    {
        Value value = Value.createValue(binarySchedule.toByteArray());
        Key scheduleKey = keyMapper.mapCacheKey(id);
        kvStore.put(scheduleKey, value);
        return scheduleKey;
    }

}
