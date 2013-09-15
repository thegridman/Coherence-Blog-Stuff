package com.thegridman.coherence.pof;

import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofWriter;
import com.tangosol.io.pof.RawTime;
import com.tangosol.io.pof.reflect.Codec;

import java.io.IOException;

/**
 * @author Jonathan Knight
 */
public class RawTimeCodec implements Codec
{
    @Override
    public Object decode(PofReader pofReader, int i) throws IOException
    {
        return pofReader.readRawTime(i);
    }

    @Override
    public void encode(PofWriter pofWriter, int i, Object o) throws IOException
    {
        pofWriter.writeRawTime(i, (RawTime) o);
    }
}
