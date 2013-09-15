package com.thegridman.coherence.events;

import com.tangosol.util.MapEvent;
import com.tangosol.util.MapListener;

import java.util.LinkedList;

/**
 * @author Jonathan Knight
 */
public class MapEventCapturingListener extends LinkedList<MapEvent> implements MapListener
{

    public MapEventCapturingListener()
    {
    }

    @Override
    public void entryDeleted(MapEvent mapEvent)
    {
        add(mapEvent);
    }

    @Override
    public void entryInserted(MapEvent mapEvent)
    {
        add(mapEvent);
    }

    @Override
    public void entryUpdated(MapEvent mapEvent)
    {
        add(mapEvent);
    }
}
