package com.thegridman.coherence.events;

import com.tangosol.util.Base;
import com.tangosol.util.MapEvent;
import com.tangosol.util.MapListener;

/**
 * @author Jonathan Knight
 */
public class PrintingMapListener extends Base implements MapListener
{
    @Override
    public void entryDeleted(MapEvent mapEvent)
    {
        out(mapEvent);
    }

    @Override
    public void entryInserted(MapEvent mapEvent)
    {
        out(mapEvent);
    }

    @Override
    public void entryUpdated(MapEvent mapEvent)
    {
        out(mapEvent);
    }
}
