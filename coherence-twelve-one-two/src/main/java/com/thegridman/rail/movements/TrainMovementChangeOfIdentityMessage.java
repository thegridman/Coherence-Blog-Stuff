package com.thegridman.rail.movements;

import com.tangosol.io.pof.annotation.Portable;
import com.tangosol.io.pof.annotation.PortableProperty;

/**
 * NOTE - Change of Identity messages are only sent for freight trains.
 * One or more Change of Identity messages may be sent for a freight train, after activation,
 * where the class of the train is to change. This will happen if the train will run without
 * wagons (i.e. a Class 6 service runs as a Class 0), or if the train is carrying a wagon with
 * a defect and must run at a slower speed.
 *
 * @author Jonathan Knight
 */
@Portable
public class TrainMovementChangeOfIdentityMessage extends TrainMovementMessage
{
    /**
     * If this is the second or subsequent change of identity for a train, this field will contain
     * the revised_train_id field from the previous change of identity message
     */
    @PortableProperty(value = 101)
    private String current_train_id;

    /**
     * The new 10-character unique identity for this train
     */
    @PortableProperty(value = 102)
    private String revised_train_id;

    /**
     * The time, in milliseconds, when the train's identity was changed
     */
    @PortableProperty(value = 103)
    private Long event_timestamp;

    public TrainMovementChangeOfIdentityMessage()
    {
        this(null);
    }

    public TrainMovementChangeOfIdentityMessage(String train_id)
    {
        super(train_id);
        MessageHeader header = getHeader();
        header.setMsgType(TrainMovementMessageType.ChangeOfIdentity);
    }

    public String getCurrentTrainId()
    {
        return current_train_id;
    }

    public String getRevisedTrainId()
    {
        return revised_train_id;
    }

    public Long getEvent_timestamp()
    {
        return event_timestamp;
    }
}
