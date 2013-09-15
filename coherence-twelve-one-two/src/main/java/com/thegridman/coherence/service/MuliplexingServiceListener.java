package com.thegridman.coherence.service;

import com.tangosol.util.Base;
import com.tangosol.util.ServiceEvent;
import com.tangosol.util.ServiceListener;

/**
 * @author Jonathan Knight
 */
public abstract class MuliplexingServiceListener extends Base implements ServiceListener
{
    protected MuliplexingServiceListener()
    {
    }

    @Override
    public void serviceStarted(ServiceEvent serviceEvent)
    {
        onEvent(serviceEvent);
    }

    @Override
    public void serviceStarting(ServiceEvent serviceEvent)
    {
        onEvent(serviceEvent);
    }

    @Override
    public void serviceStopping(ServiceEvent serviceEvent)
    {
        onEvent(serviceEvent);
    }

    @Override
    public void serviceStopped(ServiceEvent serviceEvent)
    {
        onEvent(serviceEvent);
    }

    protected abstract void onEvent(ServiceEvent serviceEvent);
}
