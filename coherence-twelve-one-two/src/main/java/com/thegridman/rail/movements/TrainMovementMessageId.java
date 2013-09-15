package com.thegridman.rail.movements;

import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofSerializer;
import com.tangosol.io.pof.PofWriter;
import com.tangosol.net.cache.KeyAssociation;
import com.thegridman.rail.schedules.ScheduleId;

import java.io.IOException;

/**
 * @author Jonathan Knight
 */
public class TrainMovementMessageId implements KeyAssociation
{
    /**
     * The 10-character unique identity for this train
     * This is used in other TRUST messages to identify the train.
     * The train activation message links the train_id with a particular schedule.
     * train_id is of the format nnHHHHsXXX where:
     *     nn   is the first two digits of the origin TIPLOC, and represents the area where the train starts
     *     HHHH is the signalling ID (headcode) used within the data feeds to represent the train -
     *          for passenger trains this is the actual service headcode, but an obfusticated
     *          number is given for freight services.
     *          This is used by other feeds, e.g. the TD feed, to track trains.
     *     s    is the speed class of the train (see speed classes.)
     *     XXX is three digits of uncertain meaning
    */
    private String train_id;

    private TrainMovementMessageType msgType;

    private ScheduleId scheduleId;

    public TrainMovementMessageId(String train_id)
    {
        this(train_id, null);
    }

    public TrainMovementMessageId(String train_id, ScheduleId scheduleId)
    {
        this.train_id = train_id;
        this.scheduleId = scheduleId;
    }

    public String getTrainId()
    {
        return train_id;
    }

    public ScheduleId getScheduleId()
    {
        return scheduleId;
    }

    public void setScheduleId(ScheduleId scheduleId)
    {
        this.scheduleId = scheduleId;
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

        TrainMovementMessageId that = (TrainMovementMessageId) o;

        if (train_id != null ? !train_id.equals(that.train_id) : that.train_id != null)
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return train_id != null ? train_id.hashCode() : 0;
    }

    @Override
    public String toString()
    {
        return "TrainMovementMessageId{" +
               "train_id='" + train_id + '\'' +
               '}';
    }

    public static class Serializer implements PofSerializer
    {
        public Object deserialize(PofReader pofReader) throws IOException
        {
            String id = pofReader.readString(0);
            ScheduleId scheduleId = (ScheduleId) pofReader.readObject(1);
            pofReader.readRemainder();
            return new TrainMovementMessageId(id, scheduleId);
        }

        public void serialize(PofWriter pofWriter, Object o) throws IOException
        {
            pofWriter.writeString(0, ((TrainMovementMessageId)o).train_id);
            pofWriter.writeObject(1, ((TrainMovementMessageId) o).scheduleId);
            pofWriter.writeRemainder(null);
        }
    }
}
