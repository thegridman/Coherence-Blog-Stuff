package com.thegridman.rail.schedules;

import com.tangosol.io.pof.annotation.Portable;
import com.tangosol.io.pof.annotation.PortableProperty;
import com.thegridman.rail.schedules.enums.TransactionType;

/**
 * @author Jonathan Knight
 */
@Portable
public class Tiploc implements ScheduleEntry
{
    @PortableProperty(value = 1)
    private TransactionType transaction_type;

    @PortableProperty(value = 2)
    private String tiploc_code;

    @PortableProperty(value = 3)
    private String nalco;

    @PortableProperty(value = 4)
    private String stanox;

    @PortableProperty(value = 5)
    private String crs_code;

    @PortableProperty(value = 6)
    private String description;

    @PortableProperty(value = 7)
    private String tps_description;

    public Tiploc()
    {
    }

    public TransactionType getTransactionType()
    {
        return transaction_type;
    }

    public String getTiplocCode()
    {
        return tiploc_code;
    }

    public String getNalco()
    {
        return nalco;
    }

    public String getStanox()
    {
        return stanox;
    }

    public String getCrsCode()
    {
        return crs_code;
    }

    public String getDescription()
    {
        return description;
    }

    public String getTpsDescription()
    {
        return tps_description;
    }

    @Override
    public String toString()
    {
        return "Tiploc{" +
               "transaction_type='" + transaction_type + '\'' +
               ", tiploc_code='" + tiploc_code + '\'' +
               ", nalco='" + nalco + '\'' +
               ", stanox='" + stanox + '\'' +
               ", crs_code='" + crs_code + '\'' +
               ", description='" + description + '\'' +
               ", tps_description='" + tps_description + '\'' +
               '}';
    }
}
