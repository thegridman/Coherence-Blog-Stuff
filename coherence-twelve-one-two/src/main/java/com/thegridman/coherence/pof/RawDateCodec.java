package com.thegridman.coherence.pof;

import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofWriter;
import com.tangosol.io.pof.RawDate;
import com.tangosol.io.pof.reflect.Codec;

import java.io.IOException;

/**
 * @author Jonathan Knight
 */
public class RawDateCodec implements Codec
{
    @Override
    public Object decode(PofReader pofReader, int i) throws IOException
    {
        return pofReader.readRawDate(i);
    }

    @Override
    public void encode(PofWriter pofWriter, int i, Object o) throws IOException
    {
        pofWriter.writeRawDate(i, (RawDate)o);
    }
}
