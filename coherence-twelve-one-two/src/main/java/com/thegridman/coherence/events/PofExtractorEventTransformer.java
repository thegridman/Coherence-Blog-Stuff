package com.thegridman.coherence.events;

import com.tangosol.util.InvocableMapHelper;
import com.tangosol.util.MapEvent;
import com.tangosol.util.ValueExtractor;
import com.tangosol.util.transformer.ExtractorEventTransformer;

import java.util.Map;

/**
 * @author Jonathan Knight
 */
public class PofExtractorEventTransformer extends ExtractorEventTransformer
{
    public PofExtractorEventTransformer()
    {
    }

    public PofExtractorEventTransformer(ValueExtractor extractor)
    {
        super(extractor);
    }

    public PofExtractorEventTransformer(ValueExtractor extractorOld, ValueExtractor extractorNew)
    {
        super(extractorOld, extractorNew);
    }

    @Override
    public MapEvent transform(MapEvent event)
    {
        Object oldValue = null;
        ValueExtractor extractorOld = getOldValueExtractor();
        if (extractorOld != null)
        {
            oldValue = extractValue(extractorOld, event.getOldEntry());
        }

        Object newValue = null;
        ValueExtractor extractorNew = getNewValueExtractor();
        if (extractorNew != null)
        {
            newValue = extractValue(extractorNew, event.getNewEntry());
        }

        return new MapEvent(event.getMap(),
                            event.getId(),
                            event.getKey(),
                            oldValue,
                            newValue);
    }

    private Object extractValue(ValueExtractor extractor, Map.Entry entry)
    {
        return InvocableMapHelper.extractFromEntry(extractor, entry);
    }

}
