package com.thegridman.rail.movements;

import com.tangosol.net.ConfigurableCacheFactory;
import com.tangosol.net.NamedCache;
import com.thegridman.rail.RailConstants;
import com.thegridman.rail.schedules.ScheduleId;

/**
 * @author Jonathan Knight
 */
public class MovementsDAO
{
    private ConfigurableCacheFactory cacheFactory;

    public MovementsDAO(ConfigurableCacheFactory cacheFactory)
    {
        this.cacheFactory = cacheFactory;
    }

    public void setTrainScheduleMapping(String trainId, ScheduleId scheduleId)
    {
        NamedCache cache = cacheFactory.ensureCache(RailConstants.CACHENAME_TRAIN_SCHEDULE_MAP, null);
        cache.put(trainId, scheduleId);
    }

    public ScheduleId getScheduleIdForTrainId(String trainId)
    {
        NamedCache cache = cacheFactory.ensureCache(RailConstants.CACHENAME_TRAIN_SCHEDULE_MAP, null);
        return (ScheduleId) cache.get(trainId);
    }

    public void saveMessage(TrainMovementMessage message)
    {
        NamedCache cache = cacheFactory.ensureCache(RailConstants.CACHENAME_TRAIN_MOVEMENTS, null);
        cache.put(message.getId(), message);
    }
}
