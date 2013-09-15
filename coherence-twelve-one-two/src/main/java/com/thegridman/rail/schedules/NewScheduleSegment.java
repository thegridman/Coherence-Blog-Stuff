package com.thegridman.rail.schedules;

import com.tangosol.io.pof.annotation.Portable;
import com.tangosol.io.pof.annotation.PortableProperty;

/**
 * @author Jonathan Knight
 */
@Portable
public class NewScheduleSegment
{
    @PortableProperty(value = 1)
    private String uic_code;

    @PortableProperty(value = 2)
    private String traction_class;

    public NewScheduleSegment()
    {
    }
}
