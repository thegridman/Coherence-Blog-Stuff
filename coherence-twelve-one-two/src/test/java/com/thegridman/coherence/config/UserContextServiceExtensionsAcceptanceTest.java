package com.thegridman.coherence.config;

import com.oracle.tools.junit.AbstractTest;
import com.tangosol.net.CacheFactory;
import com.tangosol.net.ConfigurableCacheFactory;
import com.tangosol.net.InvocationService;
import com.tangosol.net.NamedCache;
import com.tangosol.net.Service;
import com.tangosol.util.ResourceRegistry;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * @author Jonathan Knight
 */
public class UserContextServiceExtensionsAcceptanceTest extends AbstractTest
{
    private static ConfigurableCacheFactory cacheFactory;

    @BeforeClass
    public static void createCacheFactory()
    {
        System.setProperty("tangosol.coherence.localhost", "Jonathans-MacBook-Pro.local");
        System.setProperty("tangosol.coherence.cacheconfig", "usercontext-extensions-cache-config.xml");

        cacheFactory = CacheFactory.getCacheFactoryBuilder().getConfigurableCacheFactory(null);
    }

    @Test
    public void shouldContainTestResourceFromInstance() throws Exception
    {
        Service service = cacheFactory.ensureService("DistributedCache");
        ResourceRegistry registry = (ResourceRegistry) service.getUserContext();

        MyTestResource resource = registry.getResource(MyTestResource.class, "test-resource");
        assertThat(resource, is(notNullValue()));
        assertThat(resource.getParamValue(), is("from instance element"));
    }

    @Test
    public void shouldContainTestResourceFromSchemeRef() throws Exception
    {
        Service service = cacheFactory.ensureService("DistributedCache");
        ResourceRegistry registry = (ResourceRegistry) service.getUserContext();

        MyTestResource resource = registry.getResource(MyTestResource.class, "another-test");
        assertThat(resource, is(notNullValue()));
        assertThat(resource.getParamValue(), is("from class scheme"));
    }

    @Test
    public void shouldContainCacheReference() throws Exception
    {
        Service service = cacheFactory.ensureService("DistributedCache");
        ResourceRegistry registry = (ResourceRegistry) service.getUserContext();

        NamedCache replicatedCache = registry.getResource(NamedCache.class, "replicated-test-cache");
        assertThat(replicatedCache, is(notNullValue()));
        assertThat(replicatedCache.getCacheName(), is("replicated-test"));
    }

    @Test
    public void shouldContainInvocationService() throws Exception
    {
        Service service = cacheFactory.ensureService("DistributedCache");
        ResourceRegistry registry = (ResourceRegistry) service.getUserContext();

        InvocationService invocationService = registry.getResource(InvocationService.class, "invocation");
        assertThat(invocationService, is(notNullValue()));
        assertThat(invocationService.getInfo().getServiceName(), is("MyInvocationService"));
    }

}
