package com.thegridman.rail.movements;

import com.tangosol.io.pof.annotation.Portable;

/**
 * Not used in Network Rail Production system
 *
 * @author Jonathan Knight
 */
@Portable
public class TrainMovementUnidentifiedTrainMessage extends TrainMovementMessage
{
    public TrainMovementUnidentifiedTrainMessage()
    {
        this(null);
    }

    public TrainMovementUnidentifiedTrainMessage(String train_id)
    {
        super(train_id);
        MessageHeader header = getHeader();
        header.setMsgType(TrainMovementMessageType.UnidentifiedTrain);
    }
}
