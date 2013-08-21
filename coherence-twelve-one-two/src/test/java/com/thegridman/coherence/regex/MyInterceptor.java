package com.thegridman.coherence.regex;

import com.tangosol.net.CacheFactory;
import com.tangosol.net.events.EventInterceptor;
import com.tangosol.net.events.annotation.Interceptor;
import com.tangosol.net.events.partition.cache.EntryEvent;

/**
 * @author Jonathan Knight
 */
@Interceptor(identifier = "MyInterceptor",
             entryEvents = {EntryEvent.Type.INSERTING, EntryEvent.Type.INSERTED},
             order = Interceptor.Order.HIGH)
public class MyInterceptor implements EventInterceptor<EntryEvent>
{
    private String name;

    public MyInterceptor(String name)
    {
        this.name = name;
    }

    @Override
    public void onEvent(EntryEvent entryEvent)
    {
        String serviceName = entryEvent.getDispatcher().getBackingMapContext().getManagerContext().getCacheService().getInfo().getServiceName();
        CacheFactory.log("MyInterceptor[" + name + "] onEvent: service=" + serviceName + " event=" + entryEvent);
    }
}
