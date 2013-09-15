package com.thegridman.rail.schedules;

import com.tangosol.io.pof.annotation.Portable;
import com.tangosol.io.pof.annotation.PortableProperty;

import java.util.List;

/**
 * @author Jonathan Knight
 */
@Portable
public class ScheduleSegment
{
    @PortableProperty(value = 1)
    private String CIF_train_category;

    @PortableProperty(value = 2)
    private String signalling_id;

    @PortableProperty(value = 3)
    private String CIF_headcode;

    @PortableProperty(value = 4)
    private String CIF_course_indicator;

    @PortableProperty(value = 5)
    private String CIF_train_service_code;

    @PortableProperty(value = 6)
    private String CIF_business_sector;

    @PortableProperty(value = 7)
    private String CIF_power_type;

    @PortableProperty(value = 8)
    private String CIF_timing_load;

    @PortableProperty(value = 9)
    private String CIF_speed;

    @PortableProperty(value = 10)
    private String CIF_operating_characteristics;

    @PortableProperty(value = 11)
    private String CIF_train_class;

    @PortableProperty(value = 12)
    private String CIF_sleepers;

    @PortableProperty(value = 13)
    private String CIF_reservations;

    @PortableProperty(value = 14)
    private String CIF_connection_indicator;

    @PortableProperty(value = 15)
    private String CIF_catering_code;

    @PortableProperty(value = 16)
    private String CIF_service_branding;

    @PortableProperty(value = 17)
    private List<Location> schedule_location;
    private List<Location> locations;

    public ScheduleSegment()
    {
    }

    public List<Location> getLocations()
    {
        return locations;
    }
}
