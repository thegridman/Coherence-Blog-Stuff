package com.thegridman.rail.schedules;

import com.tangosol.io.pof.annotation.Portable;
import com.tangosol.io.pof.annotation.PortableProperty;

/**
 * @author Jonathan Knight
 */
@Portable
public class Location
{
    /**
     * The type of location record
     * LO - Originating location - location where the train service starts from
     * LI - Intermediate location
     * LT - Terminating location - where the service terminates
     */
    @PortableProperty(value = 1)
    private String record_identity;

    /**
     * The TIPLOC code of the location
     */
    @PortableProperty(value = 2)
    private String tiploc_code;

    /**
     * Numerical value of uncertain significance
     */
    @PortableProperty(value = 3)
    private String tiploc_instance;

    /**
     * WTT arrival time (LI and LT records)
     * Format: HHMMH - time given by the 24-hour clock. Optional H indicates half-minute. In local time (i.e. adjusted for BST / GMT.)
     */
    @PortableProperty(value = 4)
    private String arrival;

    /**
     * WTT departure time (LO and LI records)
     * Format: HHMMH - time given by the 24-hour clock. Optional H indicates half-minute. In local time (i.e. adjusted for BST / GMT.)
     */
    @PortableProperty(value = 5)
    private String departure;

    /**
     * WTT passing time (LI records)
     * Format: HHMMH - time given by the 24-hour clock. Optional H indicates half-minute. In local time (i.e. adjusted for BST / GMT.)
     */
    @PortableProperty(value = 6)
    private String pass;

    /**
     * Public timetable arrival time (LI and LT records)
     * Format: HHMM - time given by the 24-hour clock. In local time (i.e. adjusted for BST / GMT.)
     */
    @PortableProperty(value = 7)
    private String public_arrival;

    /**
     * Public timetable departure time (LO and LI records)
     * Format: HHMM - time given by the 24-hour clock. In local time (i.e. adjusted for BST / GMT.)
     */
    @PortableProperty(value = 8)
    private String public_departure;

    /**
     * Platform
     * A 3-character field used to denote the platform or line that the service uses.
     */
    @PortableProperty(value = 9)
    private String platform;

    /**
     * Departure line
     * A 3-character field representing the line to be used on departure from the location. The line abbreviation will be used.
     */
    @PortableProperty(value = 10)
    private String line;

    /**
     * Arrival path
     * A 3-character field representing the line to be used on arrival at the location. The line abbreviation will be used.
     */
    @PortableProperty(value = 11)
    private String path;

    /**
     * Time allowed for recovery from engineering activities
     * H - Half a minute
     * 1,1H ... 9, 9H - One, one-and-a-half through to nine, nine-and-a-half minutes
     * 10 to 59 - 10 to 59 minutes (whole minutes only)
     */
    @PortableProperty(value = 12)
    private String engineering_allowance;

    /**
     * Time allowed for pathing requirements
     * H - Half a minute
     * 1,1H ... 9, 9H - One, one-and-a-half through to nine, nine-and-a-half minutes
     * 10 to 59 - 10 to 59 minutes (whole minutes only)
     */
    @PortableProperty(value = 13)
    private String pathing_allowance;

    /**
     * Performance allowance
     * H - Half a minute
     * 1,1H ... 9, 9H - One, one-and-a-half through to nine, nine-and-a-half minutes
     */
    @PortableProperty(value = 14)
    private String performance_allowance;

    /**
     * Same as record_identity (LO, LI or LT)
     */
    @PortableProperty(value = 15)
    private String location_type;

    public Location()
    {
    }

    @Override
    public String toString()
    {
        return "Location{" +
               "record_identity='" + record_identity + '\'' +
               ", tiploc_code='" + tiploc_code + '\'' +
               ", public_arrival='" + public_arrival + '\'' +
               ", public_departure='" + public_departure + '\'' +
               ", arrival='" + arrival + '\'' +
               ", departure='" + departure + '\'' +
               ", pass='" + pass + '\'' +
               '}';
    }
}
