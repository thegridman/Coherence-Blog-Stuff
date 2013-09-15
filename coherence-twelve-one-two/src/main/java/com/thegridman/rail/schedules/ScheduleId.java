package com.thegridman.rail.schedules;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.tangosol.io.pof.annotation.Portable;
import com.tangosol.io.pof.annotation.PortableProperty;
import com.tangosol.net.cache.KeyAssociation;
import com.thegridman.rail.schedules.enums.STPIndicator;
import org.joda.time.DateTime;
import org.joda.time.Interval;

import java.lang.reflect.Type;

/**
 * @author Jonathan Knight
 */
@Portable
public class ScheduleId implements KeyAssociation, Comparable<ScheduleId>
{
    /**
     * The unique identifier for the schedule.
     */
    @PortableProperty(value = 1)
    private String trainUID;

    @PortableProperty(value = 2)
    private Interval scheduleInterval;

    /**
     * STP (short-term planning) schedule indicator
     */
    @PortableProperty(value = 3)
    private STPIndicator stpIndicator;

    public ScheduleId()
    {
    }

    public ScheduleId(String trainUID, Interval scheduleInterval, STPIndicator stpIndicator)
    {
        this.trainUID = trainUID;
        this.scheduleInterval = scheduleInterval;
        this.stpIndicator = stpIndicator;
    }

    public String getTrainUID()
    {
        return trainUID;
    }

    public DateTime getStartDate()
    {
        return scheduleInterval.getStart();
    }

    public DateTime getEndDate()
    {
        return scheduleInterval.getEnd();
    }

    public Interval getScheduleInterval()
    {
        return scheduleInterval;
    }

    public STPIndicator getStpIndicator()
    {
        return stpIndicator;
    }

    @Override
    public Object getAssociatedKey()
    {
        return trainUID;
    }

    @Override
    public int compareTo(ScheduleId other)
    {
        int result = this.trainUID.compareTo(other.trainUID);
        if (result != 0)
        {
            return result;
        }

        return this.stpIndicator.compareTo(other.stpIndicator);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }

        ScheduleId that = (ScheduleId) o;

        if (scheduleInterval != null ? !scheduleInterval.equals(that.scheduleInterval) : that.scheduleInterval != null)
        {
            return false;
        }
        if (stpIndicator != that.stpIndicator)
        {
            return false;
        }
        if (trainUID != null ? !trainUID.equals(that.trainUID) : that.trainUID != null)
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = trainUID != null ? trainUID.hashCode() : 0;
        result = 31 * result + (scheduleInterval != null ? scheduleInterval.hashCode() : 0);
        result = 31 * result + (stpIndicator != null ? stpIndicator.hashCode() : 0);
        return result;
    }

    @Override
    public String toString()
    {
        return "ScheduleId{" +
               "trainUID='" + trainUID + '\'' +
               ", scheduleInterval=" + scheduleInterval +
               ", stpIndicator=" + stpIndicator +
               '}';
    }

    public static class JsonAdapter implements JsonDeserializer<ScheduleId>
    {
        @Override
        public ScheduleId deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException
        {
            ScheduleId scheduleId = new ScheduleId();
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            scheduleId.trainUID = jsonObject.get("CIF_train_uid").getAsString();
            scheduleId.stpIndicator = context.deserialize(jsonObject.get("CIF_stp_indicator"), STPIndicator.class);
            DateTime start = DateTime.parse(jsonObject.get("schedule_start_date").getAsString()).withTimeAtStartOfDay();
            DateTime end = DateTime.parse(jsonObject.get("schedule_end_date").getAsString()).withTimeAtStartOfDay();
            scheduleId.scheduleInterval = new Interval(start, end);
            return scheduleId;
        }
    }

}
