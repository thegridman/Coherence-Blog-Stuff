package com.thegridman.coherence.pof;

import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofSerializer;
import com.tangosol.io.pof.PofWriter;
import org.joda.time.Interval;

import java.io.IOException;

/**
 * @author Jonathan Knight
 */
public class IntervalSerializer implements PofSerializer
{
    @Override
    public Object deserialize(PofReader pofReader) throws IOException
    {
        long start = pofReader.readLong(0);
        long end = pofReader.readLong(1);
        pofReader.readRemainder();
        return new Interval(start, end);
    }

    @Override
    public void serialize(PofWriter pofWriter, Object value) throws IOException
    {
        Interval interval = (Interval) value;
        pofWriter.writeLong(0, interval.getStartMillis());
        pofWriter.writeLong(1, interval.getEndMillis());
        pofWriter.writeRemainder(null);
    }
}
