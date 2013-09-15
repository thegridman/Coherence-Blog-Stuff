package com.thegridman.coherence.pof;


import com.tangosol.io.pof.RawDate;

import java.util.Comparator;

/**
 * @author Jonathan Knight
 */
public class RawDateComparator implements Comparator<RawDate>
{
    public static final RawDateComparator INSTANCE = new RawDateComparator();

    @Override
    public int compare(RawDate lhs, RawDate rhs)
    {
        int result = lhs.getYear() - rhs.getYear();
        if (result != 0)
        {
            return result;
        }

        result = lhs.getMonth() - rhs.getMonth();
        if (result != 0)
        {
            return result;
        }

        return lhs.getDay() - rhs.getDay();
    }
}
