package com.thegridman.rail.movements;

import com.tangosol.io.pof.annotation.Portable;
import com.tangosol.io.pof.annotation.PortableProperty;

/**
 * A train movement message is sent whenever a train arrives, passes or departs a location
 * monitored by TRUST. It records the time at which the event happens.
 *
 * Reports may be automatically generated, or manually entered.
 *
 * @author Jonathan Knight
 */
@Portable
public class TrainMovementMovementMessage extends TrainMovementMessage
{
    /**
     * The date and time that this event happened at the location
     */
    @PortableProperty(value = 101)
    private Long actual_timestamp;

    /**
     * The STANOX of the location at which this event happened
     */
    @PortableProperty(value = 102)
    private String loc_stanox;

    /**
     * The planned GBTT (passenger) date and time that the event was due to happen at this location
     */
    @PortableProperty(value = 103)
    private String gbtt_timestamp;

    /**
     * The planned date and time that this event was due to happen at this location
     */
    @PortableProperty(value = 104)
    private Long planned_timestamp;

    /**
     * If the location has been revised, the STANOX of the location in the schedule at activation time
     */
    @PortableProperty(value = 105)
    private String original_loc_stanox;

    /**
     * The planned departure time associated with the original location
     */
    @PortableProperty(value = 106)
    private Long original_loc_timestamp;

    /**
     * The planned type of event - one of "ARRIVAL", "DEPARTURE" or "DESTINATION"
     */
    @PortableProperty(value = 107)
    private String planned_event_type;

    /**
     * The type of event - either "ARRIVAL" or "DEPARTURE"
     */
    @PortableProperty(value = 108)
    private String event_type;

    /**
     * Whether the event source was "AUTOMATIC" from SMART, or "MANUAL" from TOPS or TRUST SDR
     */
    @PortableProperty(value = 109)
    private String event_source;

    /**
     * Set to "false" if this report is not a correction of a previous report, or "true" if it is
     */
    @PortableProperty(value = 110)
    private boolean correction_ind;

    /**
     * Set to "false" if this report is for a location in the schedule, or "true" if it is not
     */
    @PortableProperty(value = 111)
    private boolean offroute_ind;

    /**
     * For automatic reports, either "UP" or "DOWN" depending on the direction of travel
     */
    @PortableProperty(value = 112)
    private String direction_ind;

    /**
     * A single character (or blank) depending on the line the train is travelling on, e.g. F = Fast, S = Slow
     */
    @PortableProperty(value = 113)
    private String line_ind;

    /**
     * Two characters (including a space for a single character) or blank if the movement report
     * is associated with a platform number
     */
    @PortableProperty(value = 114)
    private String platform;

    /**
     * A single character (or blank) to indicate the exit route from this location
     */
    @PortableProperty(value = 115)
    private String route;

    /**
     * Where a train has had its identity changed, the current 10-character unique identity for this train
     */
    @PortableProperty(value = 116)
    private String current_train_id;

    /**
     * Operating company ID as per TOC Codes
     */
    @PortableProperty(value = 117)
    private String division_code;

    /**
     * The number of minutes variation from the scheduled time at this location. Off-route reports will contain "0"
     */
    @PortableProperty(value = 118)
    private Long timetable_variation;

    /**
     * One of "ON TIME", "EARLY", "LATE" or "OFF ROUTE"
     */
    @PortableProperty(value = 119)
    private String variation_status;

    /**
     * The STANOX of the location at which the next report for this train is due
     */
    @PortableProperty(value = 120)
    private String next_report_stanox;

    /**
     * The running time to the next location
     */
    @PortableProperty(value = 121)
    private String next_report_run_time;

    /**
     * Set to "true" if the train has completed its journey, or "false" otherwise
     */
    @PortableProperty(value = 122)
    private boolean train_terminated;

    /**
     * Set to "true" if this is a delay monitoring point, "false" if it is not. Off-route reports will contain "false"
     */
    @PortableProperty(value = 123)
    private boolean delay_monitoring_point;

    /**
     * The STANOX of the location that generated this report. Set to "00000" for manual and off-route reports
     */
    @PortableProperty(value = 124)
    private String reporting_stanox;

    /**
     * Set to "true" if an automatic report is expected for this location, otherwise "false"
     */
    @PortableProperty(value = 125)
    private boolean auto_expected;

    public TrainMovementMovementMessage()
    {
        this(null);
    }

    public TrainMovementMovementMessage(String train_id)
    {
        super(train_id);
        MessageHeader header = getHeader();
        header.setMsgType(TrainMovementMessageType.Movement);
    }

    public Long getActualTimestamp()
    {
        return actual_timestamp;
    }

    public boolean getAutoExpected()
    {
        return auto_expected;
    }

    public boolean getCorrectionInd()
    {
        return correction_ind;
    }

    public String getCurrentTrainId()
    {
        return current_train_id;
    }

    public boolean getDelayMonitoringPoint()
    {
        return delay_monitoring_point;
    }

    public String getDirectionInd()
    {
        return direction_ind;
    }

    public String getDivisionCode()
    {
        return division_code;
    }

    public String getEventSource()
    {
        return event_source;
    }

    public String getEventType()
    {
        return event_type;
    }

    public String getGbttTimestamp()
    {
        return gbtt_timestamp;
    }

    public String getLine()
    {
        return line_ind;
    }

    public String getLocStanox()
    {
        return loc_stanox;
    }

    public String getNextReportRunTime()
    {
        return next_report_run_time;
    }

    public String getNextReportStanox()
    {
        return next_report_stanox;
    }

    public boolean getOffrouteInd()
    {
        return offroute_ind;
    }

    public String getOriginalLocStanox()
    {
        return original_loc_stanox;
    }

    public Long getOriginalLocTimestamp()
    {
        return original_loc_timestamp;
    }

    public String getPlannedEventType()
    {
        return planned_event_type;
    }

    public Long getPlannedTimestamp()
    {
        return planned_timestamp;
    }

    public String getPlatform()
    {
        return platform;
    }

    public String getReportingStanox()
    {
        return reporting_stanox;
    }

    public String getRoute()
    {
        return route;
    }

    public Long getTimetableVariation()
    {
        return timetable_variation;
    }

    public boolean getTrainTerminated()
    {
        return train_terminated;
    }

    public String getVariation_status()
    {
        return variation_status;
    }
}
