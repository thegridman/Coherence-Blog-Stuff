package com.thegridman.rail.movements;

import com.tangosol.io.pof.annotation.Portable;
import com.tangosol.io.pof.annotation.PortableProperty;

/**
 * An activation message is produced when a train entity is created from a schedule entity by
 * the TRUST system. The train entity refers to a single run of a train on a specific day whereas
 * the schedule entity is potentially valid for several months at a time. Within TRUST, this process
 * is known as Train Call. Most trains are called automatically (auto-call) before the train is due
 * to run, and there is no specific event which triggers the call. The exception to this is for
 * schedules which are Runs as required, or Runs to terminals/yards as required (flagged with Q or Y
 * in the schedule.) By default, it is assumed that the train will not run unless the train operator
 * decides that it will; the train operator will submit a message to the TRUST system and this will
 * then cause the schedule to be activated for that day (a process is known as manual call.)
 *
 * Train activations are usually received 1 - 2 hours before the train is due to run, but these trains
 * may be manually called earlier if some details of the train are due to change.
 *
 * @author Jonathan Knight
 */
@Portable
public class TrainMovementActivationMessage extends TrainMovementMessage
{

    /**
     * The timestamp (in milliseconds since the UNIX epoch) when the train was originally created in TRUST
     */
    @PortableProperty(value = 101)
    private Long creation_timestamp;

    /**
     * The date, in YYYY-MM-DD format, that the train runs. For trains activated before midnight that run
     * after midnight, this date will be tomorrow's date.
     * Note: there is currently a problem with the tp_origin_timestamp field due to the truncation of the timestamp.
     * This only occurs during daylight savings for trains which start their journey between 0001 and 0200 the next
     * day. To work around this problem, use the date in the origin_dep_timestamp field.
     */
    @PortableProperty(value = 102)
    private String tp_origin_timestamp;

    /**
     * The unique ID of the schedule being activated - either a letter and five numbers, or a space and five
     * numbers for VSTP trains
     */
    @PortableProperty(value = 103)
    private String train_uid;

    /**
     * STANOX code for the originating location in the schedule
     */
    @PortableProperty(value = 104)
    private String sched_origin_stanox;

    /**
     * The start date of the schedule
     */
    @PortableProperty(value = 105)
    private String schedule_start_date;

    /**
     * The end date of the schedule
     */
    @PortableProperty(value = 106)
    private String schedule_end_date;

    /**
     * 	Set to C for schedules from CIF/ITPS, or V for schedules from VSTP/TOPS
     */
    @PortableProperty(value = 107)
    private String schedule_source;

    /**
     * Either C (Cancellation), N (New STP), O (STP Overlay) or P (Permanent i.e. as per the WTT/LTP)
     */
    @PortableProperty(value = 108)
    private String schedule_type;

    /**
     * The signaling ID (headcode) and speed class of the train
     * d1266_record_number	Either 00000 for a CIF/ITPS schedule, or the TOPS unique ID of the schedule
     */
    @PortableProperty(value = 109)
    private String schedule_wtt_id;

    /**
     * The STANOX code of the origin of the train
     * If the train is due to start from a location other than the scheduled origin (i.e. it is part-cancelled),
     * this will be the STANOX of the location at which the train starts. Otherwise it is the STANOX of the scheduled
     * origin location. If this field is populated, it will be typically be in response to a VAR issued through VSTP
     * or SCHEDULE.
     */
    @PortableProperty(value = 110)
    private String tp_origin_stanox;

    /**
     * The WTT time of departure from the originating location. A UNIX timestamp in milliseconds since
     * the UNIX epoch, in UTC.
     */
    @PortableProperty(value = 111)
    private Long origin_dep_timestamp;

    /**
     * Either AUTOMATIC for auto-called trains, or MANUAL for manual-called trains
     */
    @PortableProperty(value = 112)
    private String train_call_type;

    /**
     * Set to NORMAL for a train called normally, or OVERNIGHT if the train is called as part of an overnight batch
     * process to activate peak period trains early
     */
    @PortableProperty(value = 113)
    private String train_call_mode;


    public TrainMovementActivationMessage()
    {
        this(null);
    }

    public TrainMovementActivationMessage(String trainId)
    {
        super(trainId);
        MessageHeader header = getHeader();
        header.setMsgType(TrainMovementMessageType.Activation);
    }

    public long getCreationTimestamp()
    {
        return creation_timestamp;
    }

    public Long getOriginDepTimestamp()
    {
        return origin_dep_timestamp;
    }

    public String getSchedOriginStanox()
    {
        return sched_origin_stanox;
    }

    public String getScheduleEndDate()
    {
        return schedule_end_date;
    }

    public String getScheduleSource()
    {
        return schedule_source;
    }

    public String getScheduleStartDate()
    {
        return schedule_start_date;
    }

    public String getScheduleType()
    {
        return schedule_type;
    }

    public String getScheduleWttId()
    {
        return schedule_wtt_id;
    }

    public String getTpOriginStanox()
    {
        return tp_origin_stanox;
    }

    public String getTpOriginTimestamp()
    {
        return tp_origin_timestamp;
    }

    public void setTpOriginTimestamp(String timestamp)
    {
        this.tp_origin_timestamp = timestamp;
    }

    public String getTrainCallMode()
    {
        return train_call_mode;
    }

    public String getTrainCallType()
    {
        return train_call_type;
    }

    public String getTrainUid()
    {
        return train_uid;
    }

    public void setTrainUID(String train_uid)
    {
        this.train_uid = train_uid;
    }
}
