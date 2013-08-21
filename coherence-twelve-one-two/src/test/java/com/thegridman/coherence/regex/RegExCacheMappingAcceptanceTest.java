package com.thegridman.coherence.regex;

import com.tangosol.net.BackingMapContext;
import com.tangosol.net.CacheFactory;
import com.tangosol.net.CacheService;
import com.tangosol.net.ConfigurableCacheFactory;
import com.tangosol.net.NamedCache;
import com.tangosol.net.cache.LocalCache;
import com.tangosol.util.Base;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author Jonathan Knight
 */
public class RegExCacheMappingAcceptanceTest
{
    private static ConfigurableCacheFactory cacheFactory;

    @BeforeClass
    public static void setup()
    {
        cacheFactory = CacheFactory.getCacheFactoryBuilder().getConfigurableCacheFactory("regex-cache-config.xml", null);
    }

    @AfterClass
    public static void tearDown()
    {
        CacheFactory.getCacheFactoryBuilder().release(cacheFactory);
    }

    @Test
    public void shouldMatchExactRegularCoherenceMapping() throws Exception
    {
        NamedCache cache = cacheFactory.ensureCache("dist-jk", null);
        CacheService service = cache.getCacheService();
        assertThat(service.getInfo().getServiceName(), is("DistributedService"));
    }

    @Test
    public void shouldMatchRegExWithMatchingSuffix() throws Exception
    {
        NamedCache cache = cacheFactory.ensureCache("dist-test", null);
        CacheService service = cache.getCacheService();
        assertThat(service.getInfo().getServiceName(), is("RegExOneDistributedService"));
    }

    @Test
    public void shouldPassInitParams() throws Exception
    {
        String cacheName = "dist-test";
        NamedCache cache = cacheFactory.ensureCache(cacheName, null);
        CacheService service = cache.getCacheService();
        BackingMapContext backingMapContext =
                service.getBackingMapManager().getContext().getBackingMapContext(cacheName);
        LocalCache backingMap = (LocalCache) backingMapContext.getBackingMap();
        assertThat(backingMap.getExpiryDelay(), is((int)Base.parseTime("2s")));
    }

    @Test
    public void shouldMatchRegExWithMatchingPrefix() throws Exception
    {
        NamedCache cache = cacheFactory.ensureCache("test-cache", null);
        CacheService service = cache.getCacheService();
        assertThat(service.getInfo().getServiceName(), is("RegExTwoDistributedService"));
    }

    @Test
    public void shouldMatchRegEx() throws Exception
    {
        NamedCache cache = cacheFactory.ensureCache("dist-test-cache", null);
        CacheService service = cache.getCacheService();
        assertThat(service.getInfo().getServiceName(), is("RegExThreeDistributedService"));
    }

}
