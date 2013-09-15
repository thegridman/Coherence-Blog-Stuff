package com.thegridman.rail.movements;

import com.tangosol.io.pof.annotation.Portable;
import com.tangosol.io.pof.annotation.PortableProperty;

/**
 * When trains are cancelled, but then reinstated, a reinstatement message message is sent.
 * This contains details of the cancellation - the location to which it applies, the type of
 * cancellation, and the reason code. As with all other messages, cancellation messages will
 * only be received for train schedules which have already been activated.
 *
 * @author Jonathan Knight
 */
@Portable
public class TrainMovementReinstatementMessage extends TrainMovementMessage
{
    /**
     * Where a train has had its identity changed, the current 10-character unique identity for this train
     */
    @PortableProperty(value = 101)
    private String  current_train_id;

    /**
     * The planned departure time associated with the original location
     */
    @PortableProperty(value = 102)
    private Long original_loc_timestamp;

    /**
     * The planned departure time at the location where the train is being reinstated
     */
    @PortableProperty(value = 103)
    private Long dep_timestamp;

    /**
     * The STANOX of the location at which the train is reinstated
     */
    @PortableProperty(value = 104)
    private String loc_stanox;

    /**
     * The STANOX of the location in the schedule at activation time, if the location has been revised
     */
    @PortableProperty(value = 105)
    private String original_loc_stanox;

    /**
     * The time at which the train was reinstated
     */
    @PortableProperty(value = 106)
    private Long reinstatement_timestamp;

    /**
     * Operating company ID as per TOC Codes
     */
    @PortableProperty(value = 107)
    private String division_code_id;

    public TrainMovementReinstatementMessage()
    {
        this(null);
    }

    public TrainMovementReinstatementMessage(String train_id)
    {
        super(train_id);
        MessageHeader header = getHeader();
        header.setMsgType(TrainMovementMessageType.Reinstatement);
    }

    public String getCurrentTrainId()
    {
        return current_train_id;
    }

    public Long getDepTimestamp()
    {
        return dep_timestamp;
    }

    public String getDivisionCodeId()
    {
        return division_code_id;
    }

    public String getLocStanox()
    {
        return loc_stanox;
    }

    public String getOriginalLocStanox()
    {
        return original_loc_stanox;
    }

    public Long getOriginalLocTimestamp()
    {
        return original_loc_timestamp;
    }

    public Long getReinstatementTimestamp()
    {
        return reinstatement_timestamp;
    }
}
