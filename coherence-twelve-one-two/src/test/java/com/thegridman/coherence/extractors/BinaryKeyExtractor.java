package com.thegridman.coherence.extractors;

import com.tangosol.io.pof.annotation.Portable;
import com.tangosol.util.BinaryEntry;
import com.tangosol.util.extractor.AbstractExtractor;

import java.util.Map;

/**
 * @author Jonathan Knight
 */
@Portable
public class BinaryKeyExtractor extends AbstractExtractor
{
    @Override
    public Object extractFromEntry(Map.Entry entry)
    {
        return ((BinaryEntry) entry).getBinaryKey();
    }
}
