package com.thegridman.rail;

import com.oracle.tools.deferred.DeferredAssert;
import com.oracle.tools.junit.AbstractTest;
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
import com.thegridman.rail.gson.RailGsonParser;
import com.thegridman.rail.kv.KVStoreScheduleHandler;
import com.thegridman.rail.schedules.ScheduleLoader;
import com.thegridman.rail.schedules.Tiploc;
import oracle.kv.KVStore;
import oracle.kv.coherence.KVLiteLauncher;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import javax.management.ObjectName;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import static com.oracle.tools.deferred.DeferredHelper.eventually;
import static com.oracle.tools.deferred.DeferredHelper.invoking;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * @author Jonathan Knight
 */
public abstract class BaseRailAcceptanceTest extends AbstractTest
{
    public static final String TEST_POF_CONFIG = "my-pof-config.xml";

    protected static KVLiteLauncher kvLite;
    protected static Cluster cluster;
    protected static KVStore store;
    protected static ConfigurablePofContext pofContext;
    protected ConfigurableCacheFactory clientCacheFactory;

    @SuppressWarnings("unchecked")
    @BeforeClass
    public static void startCluster() throws Exception
    {
        Container.start();
        pofContext = new ConfigurablePofContext(TEST_POF_CONFIG);

        String hostName = InetAddressHelper.getLocalHost().getHostName();

        AvailablePortIterator kvPorts = new AvailablePortIterator(50000);
        AvailablePortIterator coherencePorts = new AvailablePortIterator(40000);

        ContainerBasedJavaApplicationBuilder builder = new ContainerBasedJavaApplicationBuilder();

        kvLite = new KVLiteLauncher("./target/kvroot");
        kvLite.startKVLite(builder, hostName, kvPorts);

        ClusterMemberSchema storage = createStorageNodeSchema(hostName, coherencePorts, kvLite.getSystemProperties());
        ClusterMemberSchema extend = createExtendProxySchema(hostName, coherencePorts, kvLite.getSystemProperties());

        ClusterBuilder clusterBuilder = new ClusterBuilder();
        clusterBuilder.addBuilder(builder, storage, "Data", 2);
        clusterBuilder.addBuilder(builder, extend, "Proxy", 1);

        cluster = clusterBuilder.realize(new SystemApplicationConsole());

        store = kvLite.getKVStore();
    }

    @Before
    public void setupTest() throws Exception
    {
        // Assert the cluster is ready
        assertThat(cluster, is(notNullValue()));
        DeferredAssert.assertThat(eventually(invoking(cluster).getClusterSize()), is(3));

        // Assert the Proxy Service is running
        assertServiceIsRunning("Proxy-1", "TcpProxyService");

        // Get the extend port the proxy is using
        ClusterMember proxyNode = cluster.getApplication("Proxy-1");
        String extendPort = proxyNode.getSystemProperty("tangosol.coherence.extend.port");

        // Create the client properties
        Properties properties = new Properties(System.getProperties());
        properties.setProperty(ClusterMemberSchema.PROPERTY_POF_CONFIG, TEST_POF_CONFIG);
        properties.setProperty("tangosol.coherence.extend.port", extendPort);

        // Create the client Cache Factory
        clientCacheFactory = createClientCacheFactory("my-client-config.xml", properties);
    }

    protected NamedCache getCache(String cacheName)
    {
        return clientCacheFactory.ensureCache(cacheName, null);
    }

    @After
    public void shutdownClient()
    {
        if (clientCacheFactory != null)
        {
            CacheFactory.getCacheFactoryBuilder().release(clientCacheFactory);
            clientCacheFactory = null;
        }
    }

    @AfterClass
    public static void stopCluster() throws Exception
    {
        if (cluster != null)
        {
            cluster.destroy();
            cluster = null;
        }

        if (store != null)
        {
            store.close();
        }

        if (kvLite != null)
        {
            kvLite.stopKVLite();
        }
    }

    protected static void loadScheduleFile(String scheduleFile) throws Exception
    {
        KVStoreScheduleHandler handler = new KVStoreScheduleHandler(store, pofContext);
        ScheduleLoader loader = new ScheduleLoader(scheduleFile, new RailGsonParser());
        loader.load(handler);
    }

    protected void loadTiplocCache(String tiplocFileName) throws IOException
    {
        final NamedCache tiplocCache = clientCacheFactory.ensureCache(RailConstants.CACHENAME_TIPLOC, null);
        ScheduleLoader loader = new ScheduleLoader(tiplocFileName, new RailGsonParser());
        loader.load(new ScheduleLoader.DefaultHandler()
        {
            @Override
            public void handleTiploc(Tiploc tiploc, String json) throws IOException
            {
                switch (tiploc.getTransactionType())
                {
                    case Create:
                        tiplocCache.put(tiploc.getTiplocCode(), tiploc);
                        break;
                    case Delete:
                        tiplocCache.remove(tiploc.getTiplocCode());
                        break;
                }
            }
        });
    }

    public static ClusterMemberSchema createStorageNodeSchema(String hostName, AvailablePortIterator ports, Properties properties) throws Exception
    {
        return createCommonSchema(hostName, ports, properties)
                .setStorageEnabled(true);
    }

    public static ClusterMemberSchema createExtendProxySchema(String hostName, AvailablePortIterator ports, Properties properties) throws Exception
    {
        return createCommonSchema(hostName, ports, properties)
                .setStorageEnabled(false)
                .setSystemProperty("tangosol.coherence.extend.enabled", true)
                .setSystemProperty("tangosol.coherence.extend.port", ports);
    }

    public static ClusterMemberSchema createCommonSchema(String hostName, AvailablePortIterator ports, Properties properties) throws Exception
    {
        String storeName = properties.getProperty(KVLiteLauncher.PROP_KV_STORENAME);
        String kvHostName = properties.getProperty(KVLiteLauncher.PROP_KV_HELPERHOSTS);

        return new ClusterMemberSchema()
                .setSystemProperty(KVLiteLauncher.PROP_KV_STORENAME, storeName)
                .setSystemProperty(KVLiteLauncher.PROP_KV_HELPERHOSTS, kvHostName)
                .setCacheConfigURI("rail/rail-cache-config.xml")
                .setPofEnabled(true)
                .setPofConfigURI(TEST_POF_CONFIG)
                .setClusterPort(12345)
                .setJMXManagementMode(ClusterMemberSchema.JMXManagementMode.LOCAL_ONLY)
                .setJMXPort(ports)
                .setRMIServerHostName(hostName)
                .setRemoteJMXManagement(true)
                .setSingleServerMode()
                .setPreferIPv4(true);
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
