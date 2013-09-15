package com.thegridman.rail.schedules;

import com.tangosol.io.pof.annotation.Portable;
import com.tangosol.io.pof.annotation.PortableProperty;
import com.thegridman.rail.schedules.enums.TransactionType;

import java.util.List;

/**
 * @author Jonathan Knight
 */
@Portable
public class Schedule implements ScheduleEntry
{
    @PortableProperty(value = 1)
    private ScheduleId id;

    /**
     * The transaction type - create or delete
     */
    @PortableProperty(value = 2)
    private TransactionType transaction_type;

    /**
     * A seven-character field;
     * character 1 represents Monday,
     * character 7 represents Sunday.
     * A 1 in a character position means that the service
     * runs on that day, while a 0 means that it does not.
     */
    @PortableProperty(value = 3)
    private String schedule_days_runs;

    /**
     * Bank holiday running code
     * X - Does not run on specified Bank Holiday Mondays
     * G - Does not run on Glasgow Bank Holidays
     */
    @PortableProperty(value = 4)
    private String CIF_bank_holiday_running;

    /**
     * The train status code
     */
    @PortableProperty(value = 5)
    private String train_status;

    /**
     * The ATOC code
     */
    @PortableProperty(value = 6)
    private String atoc_code;

    /**
     * applicable_timetable	Applicable timetable service?
     * Y - Train is subject to performance monitoring (Applicable Timetable Service)
     * N - Train is not subject to performance monitoring (Not Applicable Timetable Service)
     * schedule_location	Field containing an array of location records
     */
    @PortableProperty(value = 7)
    private String applicable_timetable;

    @PortableProperty(value = 8)
    private ScheduleSegment schedule_segment;

    @PortableProperty(value = 9)
    private NewScheduleSegment new_schedule_segment;

    public Schedule()
    {
    }

    public Schedule(ScheduleId id)
    {
        this.id = id;
    }

    public ScheduleId getId()
    {
        return id;
    }

    public void setId(ScheduleId id)
    {
        this.id = id;
    }

    public List<Location> getLocations()
    {
        return schedule_segment.getLocations();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }

        Schedule schedule = (Schedule) o;

        if (id != null ? !id.equals(schedule.id) : schedule.id != null)
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString()
    {
        return "Schedule{" +
               "id=" + id +
               '}';
    }
}
