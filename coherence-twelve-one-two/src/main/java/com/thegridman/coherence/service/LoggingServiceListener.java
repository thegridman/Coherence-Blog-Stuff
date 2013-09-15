package com.thegridman.coherence.service;

import com.tangosol.util.ServiceEvent;

/**
 * @author Jonathan Knight
 */
public class LoggingServiceListener extends MuliplexingServiceListener
{
    @Override
    protected void onEvent(ServiceEvent serviceEvent)
    {
        log(serviceEvent);
    }
}
