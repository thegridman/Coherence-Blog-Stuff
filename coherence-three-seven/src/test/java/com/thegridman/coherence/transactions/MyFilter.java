package com.thegridman.coherence.transactions;

import com.tangosol.util.ValueExtractor;
import com.tangosol.util.filter.EqualsFilter;

import java.util.Map;

/**
 * @author Jonathan Knight
 */
public class MyFilter extends EqualsFilter
{
    public MyFilter()
    {
    }

    public MyFilter(ValueExtractor extractor, Object oValue)
    {
        super(extractor, oValue);
    }

    public MyFilter(String sMethod, double dValue)
    {
        super(sMethod, dValue);
    }

    public MyFilter(String sMethod, float fValue)
    {
        super(sMethod, fValue);
    }

    public MyFilter(String sMethod, int iValue)
    {
        super(sMethod, iValue);
    }

    public MyFilter(String sMethod, long lValue)
    {
        super(sMethod, lValue);
    }

    public MyFilter(String sMethod, Object oValue)
    {
        super(sMethod, oValue);
    }

    @Override
    public boolean evaluateEntry(Map.Entry entry)
    {
        return super.evaluateEntry(entry);
    }
}
