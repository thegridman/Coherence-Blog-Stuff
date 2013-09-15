package com.thegridman.coherence.pof;

import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofWriter;
import com.tangosol.io.pof.reflect.Codec;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;

/**
 * @author Jonathan Knight
 */
public class LinkedListCodec implements Codec
{
    @Override
    public Object decode(PofReader pofReader, int i) throws IOException
    {
        pofReader.readCollection(i, new LinkedList());
        return null;
    }

    @Override
    public void encode(PofWriter pofWriter, int i, Object o) throws IOException
    {
        pofWriter.writeCollection(i, (Collection)o);
    }
}
