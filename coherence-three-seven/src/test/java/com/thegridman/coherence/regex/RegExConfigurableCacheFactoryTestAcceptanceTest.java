package com.thegridman.coherence.regex;

import com.tangosol.net.BackingMapContext;
import com.tangosol.net.CacheFactory;
import com.tangosol.net.CacheService;
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
public class RegExConfigurableCacheFactoryTestAcceptanceTest
{
    private static final String SCOPE = "Test1";
    private static RegExConfigurableCacheFactory cacheFactory;

    @BeforeClass
    public static void setup()
    {
        cacheFactory = new RegExConfigurableCacheFactory("RegExConfigurableCacheFactoryTest.xml");
        cacheFactory.setScopeName(SCOPE);
    }

    @AfterClass
    public static void tearDown()
    {
        CacheFactory.getCacheFactoryBuilder().release(cacheFactory);
    }

    @Test
    public void shouldMatchExact() throws Exception
    {
        NamedCache cache = cacheFactory.ensureCache("dist-test", null);
        CacheService service = cache.getCacheService();
        assertThat(service.getInfo().getServiceName(), is(SCOPE + ":DistributedService"));
    }

    @Test
    public void shouldMatchSuffix() throws Exception
    {
        NamedCache cache = cacheFactory.ensureCache("dist-test-2", null);
        CacheService service = cache.getCacheService();
        assertThat(service.getInfo().getServiceName(), is(SCOPE + ":DistributedRegExOneService"));
    }

    @Test
    public void shouldMatchPrefix() throws Exception
    {
        NamedCache cache = cacheFactory.ensureCache("jk-test", null);
        CacheService service = cache.getCacheService();
        assertThat(service.getInfo().getServiceName(), is(SCOPE + ":DistributedRegExTwoService"));
    }

    @Test
    public void shouldMatchMiddle() throws Exception
    {
        NamedCache cache = cacheFactory.ensureCache("dist-jk-test", null);
        CacheService service = cache.getCacheService();
        assertThat(service.getInfo().getServiceName(), is(SCOPE + ":DistributedRegExThreeService"));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldSetInitParams() throws Exception
    {
        NamedCache cache = cacheFactory.ensureCache("dist-test-2", null);
        CacheService service = cache.getCacheService();
        BackingMapContext backingMapContext = service.getBackingMapManager().getContext().getBackingMapContext("dist-test-2");
        LocalCache localCache = (LocalCache) backingMapContext.getBackingMap();
        assertThat(localCache.getExpiryDelay(), is((int)Base.parseTime("1d")));

    }

}
