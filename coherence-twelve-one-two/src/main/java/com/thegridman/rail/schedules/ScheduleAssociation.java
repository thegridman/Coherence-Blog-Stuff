package com.thegridman.rail.schedules;

import com.tangosol.io.pof.annotation.Portable;
import com.tangosol.io.pof.annotation.PortableProperty;

/**
 * @author Jonathan Knight
 */
@Portable
public class ScheduleAssociation implements ScheduleEntry
{
    /**
     * create or delete. A full snapshot file will only contain create records. Daily updates may contain either.
     */
    @PortableProperty(value = 1)
    private String transaction_type;

    /**
     * UID of one train in the association
     */
    @PortableProperty(value = 2)
    private String main_train_uid;

    /**
     * UID of the other train in the association
     */
    @PortableProperty(value = 3)
    private String assoc_train_uid;

    /**
     * Start date of the association (in YYMMDD format)
     */
    @PortableProperty(value = 4)
    private String assoc_start_date;

    /**
     * End date for the association (in YYMMDD format)
     */
    @PortableProperty(value = 5)
    private String assoc_end_date;

    /**
     * A seven-character field;
     * character 1 represents Monday,
     * character 7 represents Sunday.
     * A 1 in a character position means that the service
     * runs on that day, while a 0 means that it does not.
     */
    @PortableProperty(value = 6)
    private String assoc_days;

    /**
     * The type of association
     * JJ - Join
     * VV - Divide
     * NP - Next
     */
    @PortableProperty(value = 7)
    private String category;

    /**
     * Date relationship of the association
     * S - Standard - the association occurs on the same day
     * N - Over next-midnight - the association occurs the next day
     * P - Over previous-midnight - the association occurs the previous day
     */
    @PortableProperty(value = 8)
    private String date_indicator;

    /**
     * IPLOC of the location where the association applies
     */
    @PortableProperty(value = 9)
    private String location;

    /**
     * Together with the TIPLOC,
     * identifies the event on the base UID
     */
    @PortableProperty(value = 10)
    private String base_location_suffix;

    /**
     * Together with the TIPLOC,
     * identifies the event on the associated UID
     */
    @PortableProperty(value = 11)
    private String assoc_location_suffix;

    /** Not Used */
    @PortableProperty(value = 12)
    private String diagram_type;

    /**
     * STP (short-term planning) schedule indicator
     * C - STP cancellation of permanent association
     * N - New STP association (not an overlay)
     * O - STP overlay of permanent association
     * P - Permanent association
     */
    @PortableProperty(value = 13)
    private String CIF_stp_indicator;

    public ScheduleAssociation()
    {
    }

    @Override
    public String toString()
    {
        return "ScheduleAssociation{" +
               "transaction_type='" + transaction_type + '\'' +
               ", main_train_uid='" + main_train_uid + '\'' +
               ", assoc_train_uid='" + assoc_train_uid + '\'' +
               ", assoc_start_date='" + assoc_start_date + '\'' +
               ", assoc_end_date='" + assoc_end_date + '\'' +
               ", assoc_days='" + assoc_days + '\'' +
               ", category='" + category + '\'' +
               ", date_indicator='" + date_indicator + '\'' +
               ", location='" + location + '\'' +
               ", base_location_suffix='" + base_location_suffix + '\'' +
               ", assoc_location_suffix='" + assoc_location_suffix + '\'' +
               ", diagram_type='" + diagram_type + '\'' +
               ", CIF_stp_indicator='" + CIF_stp_indicator + '\'' +
               '}';
    }

}
