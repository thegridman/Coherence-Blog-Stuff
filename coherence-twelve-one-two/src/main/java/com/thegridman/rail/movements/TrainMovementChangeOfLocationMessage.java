package com.thegridman.rail.movements;

import com.tangosol.io.pof.annotation.Portable;

/**
 * Not used in Network Rail Production system
 *
 * @author Jonathan Knight
 */
@Portable
public class TrainMovementChangeOfLocationMessage extends TrainMovementMessage
{
    public TrainMovementChangeOfLocationMessage()
    {
        this(null);
    }

    public TrainMovementChangeOfLocationMessage(String train_id)
    {
        super(train_id);
        MessageHeader header = getHeader();
        header.setMsgType(TrainMovementMessageType.ChangeOfLocation);
    }
}
