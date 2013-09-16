package oracle.kv.coherence;

import coherence.Person;
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
import com.tangosol.util.Binary;
import com.tangosol.util.ExternalizableHelper;
import com.tangosol.util.processor.ExtractorProcessor;
import com.thegridman.coherence.extractors.BinaryKeyExtractor;
import oracle.kv.KVStore;
import oracle.kv.Key;
import oracle.kv.ValueVersion;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.management.ObjectName;
import java.net.Inet4Address;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static com.oracle.tools.deferred.DeferredHelper.eventually;
import static com.oracle.tools.deferred.DeferredHelper.invoking;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * @author Jonathan Knight
 */
public class NoSQLAcceptanceTest extends AbstractTest
{
    private static KVLiteLauncher kvLite;
    private static Cluster cluster;
    private static KVStore store;
    private static ConfigurablePofContext pofContext;
    private ConfigurableCacheFactory clientCacheFactory;

    @SuppressWarnings("unchecked")
    @BeforeClass
    public static void startCluster() throws Exception
    {
        Container.start();
        pofContext = new ConfigurablePofContext("my-pof-config.xml");

        String hostName = Inet4Address.getLocalHost().getHostName();

        AvailablePortIterator kvPorts = new AvailablePortIterator(50000);
        AvailablePortIterator coherencePorts = new AvailablePortIterator(40000);

        ContainerBasedJavaApplicationBuilder builder = new ContainerBasedJavaApplicationBuilder();

        kvLite = new KVLiteLauncher("./target/kvroot");
        kvLite.startKVLite(builder, hostName, kvPorts);

        ClusterMemberSchema storage = createStorageNodeSchema(coherencePorts, kvLite.getSystemProperties());
        ClusterMemberSchema extend = createExtendProxySchema(coherencePorts, kvLite.getSystemProperties());

        ClusterBuilder clusterBuilder = new ClusterBuilder();
        clusterBuilder.addBuilder(builder, storage, "Data", 2);
        clusterBuilder.addBuilder(builder, extend, "Proxy", 1);

        cluster = clusterBuilder.realize(new SystemApplicationConsole());

        store = kvLite.getKVStore();
        kvLite.loadSchema("coherence/person-schema.avsc");
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
        properties.setProperty("tangosol.coherence.extend.port", extendPort);

        // Create the client Cache Factory
        clientCacheFactory = createClientCacheFactory("my-client-config.xml", properties);
    }

    @Test
    public void shouldPutDataIntoCacheAndNoSQL() throws Exception
    {
        String cacheName = "nosql-test";
        String cacheKey = "Key-1";
        String cacheValue = "Value-1";

        NamedCache cache = clientCacheFactory.ensureCache(cacheName, null);
        cache.put(cacheKey, cacheValue);
        assertThat((String) cache.get(cacheKey), is(cacheValue));

        Binary binaryKey = (Binary) cache.invoke(cacheKey, new ExtractorProcessor(new BinaryKeyExtractor()));
        Key key = Key.fromString("/" + cacheName + "/" + NoSQLBinaryStore.encodeBinaryKey(binaryKey.toByteArray()));
        ValueVersion valueVersion = store.get(key);

        assertThat(valueVersion, is(notNullValue()));

        Binary binary = new Binary(valueVersion.getValue().getValue());
        String value = (String) ExternalizableHelper.fromBinary(binary, pofContext);
        assertThat(value, is(cacheValue));
    }

    @Test
    public void shouldLoadDataBackFromNoSQL() throws Exception
    {
        String cacheName = "nosql-test";
        String cacheKey = "Key-1";
        String cacheValue = "Value-1";

        NamedCache cache = clientCacheFactory.ensureCache(cacheName, null);

        // Put value into cache with 1 milli-second expiry
        cache.put(cacheKey, cacheValue, 1L);
        // Sleep for longer than expiry
        Thread.sleep(10);
        // ensure entry is expired
        assertThat(cache.size(), is(0));

        // Get the entry back - loading it from NoSQL
        assertThat((String) cache.get(cacheKey), is(cacheValue));
    }

    @Test
    public void shouldEraseFromNoSQL() throws Exception
    {
        String cacheName = "nosql-test";
        String cacheKey = "Key-1";
        String cacheValue = "Value-1";

        NamedCache cache = clientCacheFactory.ensureCache(cacheName, null);
        cache.put(cacheKey, cacheValue);
        assertThat((String) cache.get(cacheKey), is(cacheValue));

        Binary binaryKey = (Binary) cache.invoke(cacheKey, new ExtractorProcessor(new BinaryKeyExtractor()));

        // Remove from cache - and from NoSQL
        cache.remove(cacheKey);

        Key key = Key.fromString("/" + cacheName + "/" + NoSQLBinaryStore.encodeBinaryKey(binaryKey.toByteArray()));
        ValueVersion valueVersion = store.get(key);
        assertThat(valueVersion, is(nullValue()));
    }

    @Test
    public void shouldPutAvroDataIntoCacheAndNoSQL() throws Exception
    {
        String cacheName = "nosql-person";
        Person person = createPerson();
        Key key = Key.createKey(Arrays.asList("p", "avro", "0000000001"));
        // make sure key is not in NoSQL store
        store.delete(key);

        NamedCache cache = clientCacheFactory.ensureCache(cacheName, null);
        cache.put(key, person);

        ValueVersion valueVersion = store.get(key);
        assertThat(valueVersion, is(notNullValue()));
    }

    @Test
    public void shouldPutAvroDataIntoCacheAndNoSQLAndReloadOnExpiry() throws Exception
    {
        String cacheName = "nosql-person";
        Person person = createPerson();
        Key key = Key.createKey(Arrays.asList("p", "avro", "0000000001"));
        // make sure key is not in NoSQL store
        store.delete(key);

        NamedCache cache = clientCacheFactory.ensureCache(cacheName, null);
        // put in cache with 1 milli-second expiry
        cache.put(key, person, 1L);
        // Wait longer than expiry
        Thread.sleep(10);
        // ensure cache is empty
        assertThat(cache.size(), is(0));

        Person result = (Person) cache.get(key);
        assertThat(result, is(notNullValue()));
        assertThat(result.getFirstname(), is(person.getFirstname()));
    }

    @Test
    public void shouldPutAvroDataIntoCacheAndNoSQLThenDeleteIt() throws Exception
    {
        String cacheName = "nosql-person";
        Person person = createPerson();
        Key key = Key.createKey(Arrays.asList("p", "avro", "0000000001"));
        // make sure key is not in NoSQL store
        store.delete(key);

        NamedCache cache = clientCacheFactory.ensureCache(cacheName, null);
        cache.put(key, person);
        cache.remove(key);

        ValueVersion valueVersion = store.get(key);
        assertThat(valueVersion, is(nullValue()));
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

    public static ClusterMemberSchema createStorageNodeSchema(AvailablePortIterator ports, Properties properties) throws Exception
    {
        return createCommonSchema(ports, properties)
                .setStorageEnabled(true);
    }

    public static ClusterMemberSchema createExtendProxySchema(AvailablePortIterator ports, Properties properties) throws Exception
    {
        return createCommonSchema(ports, properties)
                .setStorageEnabled(false)
                .setSystemProperty("tangosol.coherence.extend.enabled", true)
                .setSystemProperty("tangosol.coherence.extend.port", ports);
    }

    public static ClusterMemberSchema createCommonSchema(AvailablePortIterator ports, Properties properties) throws Exception
    {
        String storeName = properties.getProperty(KVLiteLauncher.PROP_KV_STORENAME);
        String kvHostName = properties.getProperty(KVLiteLauncher.PROP_KV_HELPERHOSTS);

        String localHostName = InetAddressHelper.getLocalHost().getHostName();

        return new ClusterMemberSchema()
                .setSystemProperty(KVLiteLauncher.PROP_KV_STORENAME, storeName)
                .setSystemProperty(KVLiteLauncher.PROP_KV_HELPERHOSTS, kvHostName)
                .setCacheConfigURI("my-cache-config.xml")
                .setPofEnabled(true)
                .setPofConfigURI("my-pof-config.xml")
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

    private Person createPerson()
    {

        final Person person = new Person();
        person.setFirstname("Percival");
        person.setLastname("Lowell");
        person.setPhone("650-506-7000");
        person.setAge(36);

        return person;
    }

}
