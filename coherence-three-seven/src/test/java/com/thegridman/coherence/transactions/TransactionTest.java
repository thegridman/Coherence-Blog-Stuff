package com.thegridman.coherence.transactions;

import com.oracle.tools.deferred.DeferredAssert;
import com.oracle.tools.runtime.coherence.Cluster;
import com.oracle.tools.runtime.coherence.ClusterBuilder;
import com.oracle.tools.runtime.coherence.ClusterMember;
import com.oracle.tools.runtime.coherence.ClusterMemberSchema;
import com.oracle.tools.runtime.console.SystemApplicationConsole;
import com.oracle.tools.runtime.java.ContainerBasedJavaApplicationBuilder;
import com.oracle.tools.runtime.java.container.Container;
import com.oracle.tools.runtime.network.AvailablePortIterator;
import com.tangosol.io.pof.ConfigurablePofContext;
import com.tangosol.net.CacheFactory;
import com.tangosol.net.CacheFactoryBuilder;
import com.tangosol.net.ConfigurableCacheFactory;
import com.tangosol.net.InetAddressHelper;
import com.tangosol.net.NamedCache;
import com.tangosol.run.xml.XmlDocument;
import com.tangosol.run.xml.XmlElement;
import com.tangosol.run.xml.XmlHelper;
import com.tangosol.run.xml.XmlValue;
import com.tangosol.util.extractor.ReflectionExtractor;
import com.thegridman.domain.MyValueClass;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.management.ObjectName;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import static com.oracle.tools.deferred.DeferredHelper.eventually;
import static com.oracle.tools.deferred.DeferredHelper.invoking;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * @author Jonathan Knight
 */
public class TransactionTest
{
    private static Cluster cluster;
    private static ConfigurablePofContext pofContext;
    private ConfigurableCacheFactory clientCacheFactory;

    @SuppressWarnings("unchecked")
    @BeforeClass
    public static void startCluster() throws Exception
    {
        Container.start();
        pofContext = new ConfigurablePofContext("my-pof-config.xml");

        AvailablePortIterator coherencePorts = new AvailablePortIterator(40000);

        ContainerBasedJavaApplicationBuilder builder = new ContainerBasedJavaApplicationBuilder();

        ClusterMemberSchema storage = createStorageNodeSchema(coherencePorts);
        ClusterMemberSchema extend = createExtendProxySchema(coherencePorts);

        ClusterBuilder clusterBuilder = new ClusterBuilder();
        clusterBuilder.addBuilder(builder, storage, "Data", 2);
        clusterBuilder.addBuilder(builder, extend, "Proxy", 1);

        cluster = clusterBuilder.realize(new SystemApplicationConsole());
    }

    @Before
    public void setupTest() throws Exception
    {
        // Assert the cluster is ready
        assertThat(cluster, is(notNullValue()));
        DeferredAssert.assertThat(eventually(invoking(cluster).getClusterSize()), is(3));

        System.setProperty("tangosol.coherence.serializer", "pof");
        System.setProperty(ClusterMemberSchema.PROPERTY_POF_CONFIG, "my-pof-config.xml");
        System.setProperty(ClusterMemberSchema.PROPERTY_DISTRIBUTED_LOCALSTORAGE, "false");

        // Create the client properties
        System.getProperties().putAll(cluster.getApplication("Data-1").getSystemProperties());

        // Create the client Cache Factory
        clientCacheFactory = CacheFactory.getCacheFactoryBuilder().getConfigurableCacheFactory("coherence-cache-config.xml", null);
    }

    @Test
    public void shouldDoPutToTransactionalCache() throws Exception
    {
        String cacheName = "tx-test";
        String cacheKey = "Key-1";
        MyValueClass cacheValue = new MyValueClass("One", "Two", "Three");

        NamedCache cache = clientCacheFactory.ensureCache(cacheName, null);
        cache.addIndex(new ReflectionExtractor("getFieldTwo"), false, null);
        cache.put(cacheKey, cacheValue);

        Set entries = cache.entrySet(new MyFilter(new ReflectionExtractor("getFieldTwo"), "Two"));
        assertThat(entries.size(), is(1));
    }


    @AfterClass
    public static void stopCluster() throws Exception
    {
        if (cluster != null)
        {
            cluster.destroy();
            cluster = null;
        }
    }

    public static ClusterMemberSchema createStorageNodeSchema(AvailablePortIterator ports) throws Exception
    {
        return createCommonSchema(ports)
                .setStorageEnabled(true);
    }

    public static ClusterMemberSchema createExtendProxySchema(AvailablePortIterator ports) throws Exception
    {
        return createCommonSchema(ports)
                .setStorageEnabled(false)
                .setSystemProperty("tangosol.coherence.extend.enabled", true)
                .setSystemProperty("tangosol.coherence.extend.port", ports);
    }

    public static ClusterMemberSchema createCommonSchema(AvailablePortIterator ports) throws Exception
    {
        String localHostName = InetAddressHelper.getLocalHost().getHostName();

        return new ClusterMemberSchema()
                //.setCacheConfigURI("my-cache-config.xml")
                .setPofEnabled(true)
                .setPofConfigURI("my-pof-config.xml")
                .setSystemProperty("tangosol.coherence.serializer", "pof")
                .setClusterPort(12345)
                .setJMXManagementMode(ClusterMemberSchema.JMXManagementMode.LOCAL_ONLY)
                .setJMXPort(ports)
                .setRMIServerHostName(localHostName)
                .setRemoteJMXManagement(true)
                .setSingleServerMode();
    }

    private void assertServiceIsRunning(String memberName, String serviceName) throws Exception
    {
        assertThat(cluster, is(notNullValue()));
        ClusterMember member = cluster.getApplication(memberName);
        int nodeId = member.getLocalMemberId();
        ObjectName objectName = new ObjectName(String.format("Coherence:type=Service,name=%s,nodeId=%d", serviceName, nodeId));
        DeferredAssert.assertThat(eventually(invoking(member).getMBeanAttribute(objectName, "Running", Boolean.class)), is(true));
    }

    private ConfigurableCacheFactory createClientCacheFactory(String cacheConfigurationURI, Properties properties)
    {
        XmlDocument clientConfigXml = XmlHelper.loadFileOrResource(cacheConfigurationURI, "Client");
        replacePropertiesInXml(clientConfigXml, "system-property", properties);

        CacheFactoryBuilder cacheFactoryBuilder = CacheFactory.getCacheFactoryBuilder();
        cacheFactoryBuilder.setCacheConfiguration(cacheConfigurationURI, null, clientConfigXml);
        return cacheFactoryBuilder.getConfigurableCacheFactory(cacheConfigurationURI, null);
    }

    @SuppressWarnings("unchecked")
    private void replacePropertiesInXml(XmlElement xml, String propertyAttributeName, Properties properties)
    {
        XmlValue attribute = xml.getAttribute(propertyAttributeName);
        if (attribute != null)
        {
            xml.setAttribute(propertyAttributeName, null);
            try
            {
                String propertyValue = properties.getProperty(attribute.getString());
                if (propertyValue != null)
                {
                    xml.setString(propertyValue);
                }
            }
            catch (Exception _ignored)
            {
                // ignored on purpose
            }
        }

        for (XmlElement child : (List<XmlElement>) xml.getElementList())
        {
            replacePropertiesInXml(child, propertyAttributeName, properties);
        }
    }

}
