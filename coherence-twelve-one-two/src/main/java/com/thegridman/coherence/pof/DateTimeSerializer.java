package com.thegridman.coherence.pof;

import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofSerializer;
import com.tangosol.io.pof.PofWriter;
import org.joda.time.DateTime;

import java.io.IOException;

/**
 * @author Jonathan Knight
 */
public class DateTimeSerializer implements PofSerializer
{
    @Override
    public Object deserialize(PofReader pofReader) throws IOException
    {
        long time = pofReader.readLong(0);
        pofReader.readRemainder();
        return new DateTime(time);
    }

    @Override
    public void serialize(PofWriter pofWriter, Object value) throws IOException
    {
        DateTime time = (DateTime) value;
        pofWriter.writeLong(0, time.toDate().getTime());
        pofWriter.writeRemainder(null);
    }
}
