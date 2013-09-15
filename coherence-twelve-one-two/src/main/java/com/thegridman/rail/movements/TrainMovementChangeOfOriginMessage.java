package com.thegridman.rail.movements;

import com.tangosol.io.pof.annotation.Portable;
import com.tangosol.io.pof.annotation.PortableProperty;

/**
 * When a train is due to start from a location other than the first location in the schedule,
 * a change of origin message will be sent.
 * Trains may start from alternate locations for two reasons:
 * When the previous working is terminated short of its destination and the return working will
 * start from that location
 * When the train starts from, for example, Doncaster North Yard rather than the schedule location
 * of Doncaster South Yard
 *
 * @author Jonathan Knight
 */
@Portable
public class TrainMovementChangeOfOriginMessage extends TrainMovementMessage
{
    /**
     * The planned departure time at the location where the train is being reinstated
     */
    @PortableProperty(value = 101)
    private Long dep_timestamp;

    /**
     * If the location has been revised, e.g. the new origin is 'out of plan' for the train,
     * the STANOX of location in the schedule at activation
     */
    @PortableProperty(value = 102)
    private String original_loc_stanox;

    /**
     * The planned departure time associated with the original location
     */
    @PortableProperty(value = 103)
    private Long original_loc_timestamp;

    /**
     * Always blank
     */
    @PortableProperty(value = 104)
    private String current_train_id;

    /**
     * The reason code for the cancellation, taken from the Delay Attribution Guide
     */
    @PortableProperty(value = 105)
    private String reason_code;

    /**
     * Operating company ID as per TOC Codes
     */
    @PortableProperty(value = 106)
    private String division_code;

    /**
     * The time at which the Change of Origin is entered into TRUST
     */
    @PortableProperty(value = 107)
    private Long coo_timestamp;

    public TrainMovementChangeOfOriginMessage()
    {
        this(null);
    }

    public TrainMovementChangeOfOriginMessage(String train_id)
    {
        super(train_id);
        MessageHeader header = getHeader();
        header.setMsgType(TrainMovementMessageType.ChangeOfOrigin);
    }

    public Long getCooTimestamp()
    {
        return coo_timestamp;
    }

    public String getCurrentTrainId()
    {
        return current_train_id;
    }

    public Long getDepTimestamp()
    {
        return dep_timestamp;
    }

    public String getDivisionCode()
    {
        return division_code;
    }

    public String getOriginalLocStanox()
    {
        return original_loc_stanox;
    }

    public Long getOriginalLocTimestamp()
    {
        return original_loc_timestamp;
    }

    public String getReasonCode()
    {
        return reason_code;
    }
}
