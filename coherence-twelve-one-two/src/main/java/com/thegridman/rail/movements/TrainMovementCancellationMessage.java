package com.thegridman.rail.movements;

import com.tangosol.io.pof.annotation.Portable;
import com.tangosol.io.pof.annotation.PortableProperty;

/**
 * A cancellation message is sent when the train does not, or will not, complete its scheduled journey.
 * A train may be cancelled in one of four ways:
 * At activation time ("ON CALL"), usually where the applicable schedule has a STP indicator of "C"
 * - see Planned Cancellations. Trains may be cancelled for other reasons before train has been activated,
 * and when activation occurs, the train will be immediately cancelled with the appropriate reason code
 * At the train's planned origin ("AT ORIGIN")
 * En-route ("EN ROUTE")
 * Off-route ("OUT OF PLAN")
 *
 * As with all other messages, cancellation messages will only be received for train schedules which
 * have already been activated.
 *
 * @author Jonathan Knight
 */
@Portable
public class TrainMovementCancellationMessage extends TrainMovementMessage
{
    /**
     * For an an "OUT OF PLAN" cancellation, this is the location that the train should have been at
     * according to the schedule
     */
    @PortableProperty(value = 101)
    private String orig_loc_stanox;

    /**
     * For an "OUT OF PLAN" cancellation, this is the departure time of the location that the train should have
     * been at according to the schedule
     */
    @PortableProperty(value = 102)
    private Long orig_loc_timestamp;

    /**
     * The departure time at the location that the train is cancelled from (in milliseconds since the UNIX epoch)
     */
    @PortableProperty(value = 103)
    private Long dep_timestamp;

    /**
     * Operating company ID as per TOC Codes
     */
    @PortableProperty(value = 104)
    private String division_code;

    /**
     * The STANOX of the location that the train is being cancelled from. For an "OUT OF PLAN" cancellation,
     * this STANOX will not be in the schedule, but a Train Movement message will have already been sent.
     */
    @PortableProperty(value = 105)
    private String loc_stanox;

    /**
     * The time at which the cancellation was input to TRUST
     */
    @PortableProperty(value = 106)
    private Long canx_timestamp;

    /**
     * The reason code for the cancellation, taken from the Delay Attribution Guide
     */
    @PortableProperty(value = 107)
    private String canx_reason_code;

    /**
     * Either "ON CALL" for a planned cancellation, "AT ORIGIN", "EN ROUTE" or "OUT OF PLAN"
     */
    @PortableProperty(value = 108)
    private String canx_type;

    public TrainMovementCancellationMessage()
    {
        this(null);
    }

    public TrainMovementCancellationMessage(String train_id)
    {
        super(train_id);
        MessageHeader header = getHeader();
        header.setMsgType(TrainMovementMessageType.Cancellation);
    }

    public String getCanxReasonCode()
    {
        return canx_reason_code;
    }

    public long getCanxTimestamp()
    {
        return canx_timestamp;
    }

    public String getCanxType()
    {
        return canx_type;
    }

    public Long getDepTimestamp()
    {
        return dep_timestamp;
    }

    public String getDivisionCode()
    {
        return division_code;
    }

    public String getLocStanox()
    {
        return loc_stanox;
    }

    public String getOriginalLocStanox()
    {
        return orig_loc_stanox;
    }

    public Long getOrigLocTimestamp()
    {
        return orig_loc_timestamp;
    }

}
