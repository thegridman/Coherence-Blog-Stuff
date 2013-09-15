package com.thegridman.coherence.cache;

import com.tangosol.config.annotation.Injectable;
import com.tangosol.net.cache.CacheLoader;
import com.tangosol.net.cache.LocalCache;
import com.tangosol.util.ResourceRegistry;
import com.tangosol.util.SimpleResourceRegistry;
import com.thegridman.coherence.config.NamedResourceBuilder;

import java.util.List;

/**
 * @author Jonathan Knight
 */
public class LocalCacheWithResources extends LocalCache
{
    private final ResourceRegistry resourceRegistry = new SimpleResourceRegistry();

    public LocalCacheWithResources()
    {
    }

    public LocalCacheWithResources(int cUnits)
    {
        super(cUnits);
    }

    public LocalCacheWithResources(int cUnits, int cExpiryMillis)
    {
        super(cUnits, cExpiryMillis);
    }

    public LocalCacheWithResources(int cUnits, int cExpiryMillis, CacheLoader loader)
    {
        super(cUnits, cExpiryMillis, loader);
    }

    @Injectable(value = "resources")
    public void setResourceBuilders(List<NamedResourceBuilder> resourceBuilders)
    {

    }
}
