package com.thegridman.coherence.regex;

import com.tangosol.net.events.Event;
import com.tangosol.net.events.EventDispatcher;
import com.tangosol.net.events.EventInterceptor;
import com.tangosol.net.events.annotation.Interceptor;
import com.tangosol.net.events.internal.NamedEventInterceptor;
import com.tangosol.net.events.partition.PartitionedServiceDispatcher;
import com.tangosol.net.events.partition.cache.PartitionedCacheDispatcher;
import com.tangosol.util.RegistrationBehavior;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Jonathan Knight
 */
public class RegExEventInterceptor<E extends Event<?>> extends NamedEventInterceptor<E>
{
    private Pattern pattern;

    public RegExEventInterceptor(String sName, EventInterceptor<E> interceptor,
                                 String cacheName, String serviceName, Interceptor.Order order,
                                 RegistrationBehavior behavior)
    {
        super(sName, interceptor, cacheName, serviceName, order, behavior);
        pattern = Pattern.compile(cacheName);
    }

    @Override
    public boolean isAcceptable(EventDispatcher dispatcher)
    {
        String serviceName = getServiceName();
        if (serviceName == null && pattern == null)
        {
            return true;
        }

        String dispatcherServiceName = null;
        String dispatcherCacheName = null;
        if (dispatcher instanceof PartitionedCacheDispatcher)
        {
            PartitionedCacheDispatcher bmd = (PartitionedCacheDispatcher) dispatcher;
            dispatcherServiceName = bmd.getBackingMapContext()
                    .getManagerContext().getCacheService().getInfo().getServiceName();
            dispatcherCacheName = bmd.getBackingMapContext().getCacheName();
        }
        else if ((dispatcher instanceof PartitionedServiceDispatcher))
        {
            dispatcherServiceName = ((PartitionedServiceDispatcher) dispatcher).getService()
                    .getInfo().getServiceName();
        }

        boolean match = serviceName == null || serviceName.equals(dispatcherServiceName);
        if (match && dispatcherCacheName != null && pattern != null)
        {
            Matcher matcher = pattern.matcher(dispatcherCacheName);
            return matcher.matches();
        }

        return false;
    }
}
