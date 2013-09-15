package com.thegridman.coherence.config;

import com.oracle.tools.junit.AbstractTest;
import com.oracle.tools.runtime.coherence.ClusterMemberSchema;
import com.tangosol.net.CacheService;
import com.tangosol.net.ConfigurableCacheFactory;
import com.tangosol.net.ExtensibleConfigurableCacheFactory;
import com.tangosol.net.NamedCache;
import org.junit.Test;

/**
 * @author Jonathan Knight
 */
public class ExtensionsAcceptanceTest extends AbstractTest
{
    @Test
    public void shouldCreate() throws Exception
    {
        System.setProperty(ClusterMemberSchema.PROPERTY_LOCALHOST_ADDRESS, "Jonathans-MacBook-Pro.local");

        ExtensibleConfigurableCacheFactory.Dependencies deps =
            ExtensibleConfigurableCacheFactory.DependenciesHelper.newInstance("servicelistener-extensions-cache-config.xml");
        ConfigurableCacheFactory cacheFactory = new ExtensibleConfigurableCacheFactory(deps);

        NamedCache cache = cacheFactory.ensureCache("dist-test", null);
        CacheService service = cache.getCacheService();

        System.out.println();
    }

}
