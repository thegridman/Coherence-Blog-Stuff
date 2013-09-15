package com.thegridman.coherence.events;

import com.tangosol.util.Base;
import com.tangosol.util.MapEvent;
import com.tangosol.util.MapListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author Jonathan Knight
 */
public class MultiMapListener extends Base implements MapListener
{
    private List<MapListener> listeners;

    public MultiMapListener(MapListener... listeners)
    {
        this(Arrays.asList(listeners));
    }

    public MultiMapListener(Collection<MapListener> listeners)
    {
        this.listeners = new ArrayList<>(listeners);
    }

    @Override
    public void entryDeleted(MapEvent mapEvent)
    {
        for (MapListener listener : listeners)
        {
            try
            {
                listener.entryDeleted(mapEvent);
            }
            catch (Exception e)
            {
                err(e);
            }
        }
    }

    @Override
    public void entryInserted(MapEvent mapEvent)
    {
        for (MapListener listener : listeners)
        {
            try
            {
                listener.entryInserted(mapEvent);
            }
            catch (Exception e)
            {
                err(e);
            }
        }
    }

    @Override
    public void entryUpdated(MapEvent mapEvent)
    {
        for (MapListener listener : listeners)
        {
            try
            {
                listener.entryUpdated(mapEvent);
            }
            catch (Exception e)
            {
                err(e);
            }
        }
    }
}
