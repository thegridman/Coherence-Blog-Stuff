package com.thegridman.rail.movements;

/**
 * @author Jonathan Knight
 */
public enum TrainMovementMessageType
{
    Unused,
    Activation,
    Cancellation,
    Movement,
    UnidentifiedTrain,
    Reinstatement,
    ChangeOfOrigin,
    ChangeOfIdentity,
    ChangeOfLocation,;
}
