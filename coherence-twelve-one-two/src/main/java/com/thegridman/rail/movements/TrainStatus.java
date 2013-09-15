package com.thegridman.rail.movements;

import com.tangosol.io.pof.annotation.Portable;
import com.tangosol.io.pof.annotation.PortableProperty;
import com.tangosol.net.cache.KeyAssociation;
import com.thegridman.rail.schedules.ScheduleId;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.SimpleTimeZone;

/**
 * @author Jonathan Knight
 */
@Portable
public class TrainStatus
{
    public enum Status {Activated, Early, OnTime, Delayed, Terminated, Cancelled, Reinstated}

    public static final int POF_ID = 1;
    public static final int POF_STATUS = 2;
    public static final int POF_LAST_MESSAGE_ID = 3;
    public static final int POF_LAST_MESSAGE_TIME = 4;
    public static final int POF_TIME_DIFF = 5;

    @PortableProperty(value = POF_ID)
    private Id id;

    @PortableProperty(value = POF_STATUS)
    private Status status;

    @PortableProperty(value = POF_LAST_MESSAGE_ID)
    private TrainMovementMessageId lastMessageId;

    @PortableProperty(value = POF_LAST_MESSAGE_TIME)
    private long lastMessageTime;

    @PortableProperty(value = POF_TIME_DIFF)
    private long timeDifference;

    public TrainStatus()
    {
    }

    public TrainStatus(String trainId, ScheduleId scheduleId)
    {
        this(new Id(trainId, scheduleId));
    }

    public TrainStatus(Id id)
    {
        this.id = id;
    }

    public Id getId()
    {
        return id;
    }

    public Status getStatus()
    {
        return status;
    }

    public void setStatus(Status status)
    {
        this.status = status;
    }

    public TrainMovementMessageId getLastMessageId()
    {
        return lastMessageId;
    }

    public void setLastMessageId(TrainMovementMessageId lastMessageId)
    {
        this.lastMessageId = lastMessageId;
    }

    public long getLastMessageTime()
    {
        return lastMessageTime;
    }

    public void setLastMessageTime(long lastMessageTime)
    {
        this.lastMessageTime = lastMessageTime;
    }

    public long getTimeDifference()
    {
        return timeDifference;
    }

    public void setTimeDifference(long timeDifference)
    {
        this.timeDifference = timeDifference;
    }

    @Override
    public String toString()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        sdf.setTimeZone(new SimpleTimeZone(SimpleTimeZone.UTC_TIME, "UTC"));

        return "TrainStatus{" +
               "id=" + id +
               ", status=" + status +
               ", timeDiff=" + sdf.format(new Date(Math.abs(timeDifference))) +
               ", lastMessageTime=" + String.format("%tc", lastMessageTime) +
               ", lastMessageId=" + lastMessageId +
               '}';
    }

    @Portable
    public static class Id implements KeyAssociation
    {
        public static final int POF_TRAIN_ID = 1;
        public static final int POF_SCHEDULE_ID = 2;

        @PortableProperty(value = POF_TRAIN_ID)
        private String trainId;

        @PortableProperty(value = POF_SCHEDULE_ID)
        private ScheduleId scheduleId;

        public Id()
        {
        }

        public Id(String trainId, ScheduleId scheduleId)
        {
            if (trainId == null)
            {
                throw new IllegalArgumentException("The trainId parameter cannot be null");
            }

            if (scheduleId == null)
            {
                throw new IllegalArgumentException("The scheduleId parameter cannot be null");
            }

            this.trainId = trainId;
            this.scheduleId = scheduleId;
        }

        public String getTrainId()
        {
            return trainId;
        }

        public ScheduleId getScheduleId()
        {
            return scheduleId;
        }

        @Override
        public Object getAssociatedKey()
        {
            if (scheduleId == null)
            {
                return null;
            }

            return scheduleId.getTrainUID();
        }

        @Override
        public String toString()
        {
            return "Id{" +
                   "trainId='" + trainId + '\'' +
                   ", scheduleId=" + scheduleId +
                   '}';
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

            Id id = (Id) o;

            if (!scheduleId.equals(id.scheduleId))
            {
                return false;
            }
            if (!trainId.equals(id.trainId))
            {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode()
        {
            int result = trainId.hashCode();
            result = 31 * result + scheduleId.hashCode();
            return result;
        }
    }
}
