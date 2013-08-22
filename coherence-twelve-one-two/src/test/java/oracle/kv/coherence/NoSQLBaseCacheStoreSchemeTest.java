package oracle.kv.coherence;

import com.tangosol.coherence.config.ParameterList;
import com.tangosol.coherence.config.ResolvableParameterList;
import com.tangosol.coherence.config.builder.InstanceBuilder;
import com.tangosol.coherence.config.unit.Millis;
import com.tangosol.config.expression.LiteralExpression;
import com.tangosol.config.expression.NullParameterResolver;
import com.tangosol.config.expression.ParameterResolver;
import com.tangosol.run.xml.XmlElement;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.EnumSet;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Jonathan Knight
 */
public class NoSQLBaseCacheStoreSchemeTest
{
    private ParameterResolver resolver;
    private ClassLoader classLoader;
    private ParameterList parameterList;
    private EnumSet<XmlConfigParser.ConfigOption> allOptions = EnumSet.allOf(XmlConfigParser.ConfigOption.class);
    private NoSQLStoreBase store;

    @Before
    public void setup()
    {
        classLoader = getClass().getClassLoader();
        resolver = new NullParameterResolver();
        parameterList = new ResolvableParameterList();

        store = mock(NoSQLStoreBase.class);

    }

    @Test
    public void shouldRealizeStore() throws Exception
    {
        NoSQLBaseCacheStoreScheme scheme = new NoSQLBaseCacheStoreSchemeStub(allOptions, store);
        scheme.setAvroFormat(new LiteralExpression<String>("testing"));

        NoSQLStoreBase result = scheme.realize(resolver, classLoader, parameterList);
        assertThat(result, is(sameInstance(store)));
    }

    @Test
    public void shouldSetAvroFormatIntoXmlConfig() throws Exception
    {
        String xmlValue = "testing";

        NoSQLBaseCacheStoreScheme scheme = new NoSQLBaseCacheStoreSchemeStub(allOptions, store);
        scheme.setAvroFormat(new LiteralExpression<String>(xmlValue));

        scheme.realize(resolver, classLoader, parameterList);
        assertConfigXmlOptionSet(XmlConfigParser.OPT_AVROFORMAT, xmlValue);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfAvroFormatNotAllowed() throws Exception
    {
        EnumSet<XmlConfigParser.ConfigOption> options = EnumSet.allOf(XmlConfigParser.ConfigOption.class);
        options.remove(XmlConfigParser.ConfigOption.AVRO_FORMAT);

        NoSQLBaseCacheStoreScheme scheme = new NoSQLBaseCacheStoreSchemeStub(options, store);
        scheme.setAvroFormat(new LiteralExpression<String>("bad!"));
    }

    @Test
    public void shouldSetConsistencyIntoXmlConfig() throws Exception
    {
        String xmlValue = "testing";

        NoSQLBaseCacheStoreScheme scheme = new NoSQLBaseCacheStoreSchemeStub(allOptions, store);
        scheme.setConsistency(new LiteralExpression<String>(xmlValue));

        scheme.realize(resolver, classLoader, parameterList);
        assertConfigXmlOptionSet(XmlConfigParser.OPT_CONSISTENCY, xmlValue);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfConsistencyNotAllowed() throws Exception
    {
        EnumSet<XmlConfigParser.ConfigOption> options = EnumSet.allOf(XmlConfigParser.ConfigOption.class);
        options.remove(XmlConfigParser.ConfigOption.CONSISTENCY);

        NoSQLBaseCacheStoreScheme scheme = new NoSQLBaseCacheStoreSchemeStub(options, store);
        scheme.setConsistency(new LiteralExpression<String>("bad!"));
    }

    @Test
    public void shouldSetConsistencyTimeLagIntoXmlConfig() throws Exception
    {
        NoSQLBaseCacheStoreScheme scheme = new NoSQLBaseCacheStoreSchemeStub(allOptions, store);
        scheme.setConsistencyTimeLag(new LiteralExpression<Millis>(new Millis("1234")));

        scheme.realize(resolver, classLoader, parameterList);
        assertConfigXmlOptionSet(XmlConfigParser.OPT_CONSISTENCYTIMELAG, "1234000000");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfConsistencyTimeLagNotAllowed() throws Exception
    {
        EnumSet<XmlConfigParser.ConfigOption> options = EnumSet.allOf(XmlConfigParser.ConfigOption.class);
        options.remove(XmlConfigParser.ConfigOption.CONSISTENCY);

        NoSQLBaseCacheStoreScheme scheme = new NoSQLBaseCacheStoreSchemeStub(options, store);
        scheme.setConsistencyTimeLag(new LiteralExpression<Millis>(new Millis("1234")));
    }

    @Test
    public void shouldSetConsistencyTimeoutIntoXmlConfig() throws Exception
    {
        NoSQLBaseCacheStoreScheme scheme = new NoSQLBaseCacheStoreSchemeStub(allOptions, store);
        scheme.setConsistencyTimeout(new LiteralExpression<Millis>(new Millis("1234")));

        scheme.realize(resolver, classLoader, parameterList);
        assertConfigXmlOptionSet(XmlConfigParser.OPT_CONSISTENCYTIMEOUT, "1234000000");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfConsistencyTimeoutNotAllowed() throws Exception
    {
        EnumSet<XmlConfigParser.ConfigOption> options = EnumSet.allOf(XmlConfigParser.ConfigOption.class);
        options.remove(XmlConfigParser.ConfigOption.CONSISTENCY);

        NoSQLBaseCacheStoreScheme scheme = new NoSQLBaseCacheStoreSchemeStub(options, store);
        scheme.setConsistencyTimeout(new LiteralExpression<Millis>(new Millis("1234")));
    }

    @Test
    public void shouldSetDurabilityMasterSyncIntoXmlConfig() throws Exception
    {
        String xmlValue = "testing";

        NoSQLBaseCacheStoreScheme scheme = new NoSQLBaseCacheStoreSchemeStub(allOptions, store);
        scheme.setDurabilityMasterSync(new LiteralExpression<String>(xmlValue));

        scheme.realize(resolver, classLoader, parameterList);
        assertConfigXmlOptionSet(XmlConfigParser.OPT_DURABILITYMASTERSYNC, xmlValue);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfDuarbilityMasterSyncNotAllowed() throws Exception
    {
        EnumSet<XmlConfigParser.ConfigOption> options = EnumSet.allOf(XmlConfigParser.ConfigOption.class);
        options.remove(XmlConfigParser.ConfigOption.DURABILITY);

        NoSQLBaseCacheStoreScheme scheme = new NoSQLBaseCacheStoreSchemeStub(options, store);
        scheme.setDurabilityMasterSync(new LiteralExpression<String>("bad!"));
    }

    @Test
    public void shouldSetDurabilityReplicaSyncIntoXmlConfig() throws Exception
    {
        String xmlValue = "testing";

        NoSQLBaseCacheStoreScheme scheme = new NoSQLBaseCacheStoreSchemeStub(allOptions, store);
        scheme.setDurabilityReplicaSync(new LiteralExpression<String>(xmlValue));

        scheme.realize(resolver, classLoader, parameterList);
        assertConfigXmlOptionSet(XmlConfigParser.OPT_DURABILITYREPLICASYNC, xmlValue);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfDuarbilityReplicaSyncNotAllowed() throws Exception
    {
        EnumSet<XmlConfigParser.ConfigOption> options = EnumSet.allOf(XmlConfigParser.ConfigOption.class);
        options.remove(XmlConfigParser.ConfigOption.DURABILITY);

        NoSQLBaseCacheStoreScheme scheme = new NoSQLBaseCacheStoreSchemeStub(options, store);
        scheme.setDurabilityReplicaSync(new LiteralExpression<String>("bad!"));
    }

    @Test
    public void shouldSetDurabilityReplicaAckIntoXmlConfig() throws Exception
    {
        String xmlValue = "testing";

        NoSQLBaseCacheStoreScheme scheme = new NoSQLBaseCacheStoreSchemeStub(allOptions, store);
        scheme.setDurabilityReplicaAck(new LiteralExpression<String>(xmlValue));

        scheme.realize(resolver, classLoader, parameterList);
        assertConfigXmlOptionSet(XmlConfigParser.OPT_DURABILITYREPLICAACK, xmlValue);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfDuarbilityReplicaAckNotAllowed() throws Exception
    {
        EnumSet<XmlConfigParser.ConfigOption> options = EnumSet.allOf(XmlConfigParser.ConfigOption.class);
        options.remove(XmlConfigParser.ConfigOption.DURABILITY);

        NoSQLBaseCacheStoreScheme scheme = new NoSQLBaseCacheStoreSchemeStub(options, store);
        scheme.setDurabilityReplicaAck(new LiteralExpression<String>("bad!"));
    }

    @Test
    public void shouldSetHelperHostsIntoXmlConfig() throws Exception
    {
        String xmlValue = "testing";

        NoSQLBaseCacheStoreScheme scheme = new NoSQLBaseCacheStoreSchemeStub(allOptions, store);
        scheme.setHelperHosts(new LiteralExpression<String>(xmlValue));

        scheme.realize(resolver, classLoader, parameterList);
        assertConfigXmlOptionSet(XmlConfigParser.OPT_HELPERHOSTS, xmlValue);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfHelperHostsNotAllowed() throws Exception
    {
        EnumSet<XmlConfigParser.ConfigOption> options = EnumSet.allOf(XmlConfigParser.ConfigOption.class);
        options.remove(XmlConfigParser.ConfigOption.HELPER_HOSTS);

        NoSQLBaseCacheStoreScheme scheme = new NoSQLBaseCacheStoreSchemeStub(options, store);
        scheme.setHelperHosts(new LiteralExpression<String>("bad!"));
    }

    @Test
    public void shouldSetKeyPrefixIntoXmlConfig() throws Exception
    {
        String xmlValue = "testing";

        NoSQLBaseCacheStoreScheme scheme = new NoSQLBaseCacheStoreSchemeStub(allOptions, store);
        scheme.setKeyPrefix(new LiteralExpression<String>(xmlValue));

        scheme.realize(resolver, classLoader, parameterList);
        assertConfigXmlOptionSet(XmlConfigParser.OPT_KEYPREFIX, xmlValue);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfKeyPrefixNotAllowed() throws Exception
    {
        EnumSet<XmlConfigParser.ConfigOption> options = EnumSet.allOf(XmlConfigParser.ConfigOption.class);
        options.remove(XmlConfigParser.ConfigOption.KEY_PREFIX);

        NoSQLBaseCacheStoreScheme scheme = new NoSQLBaseCacheStoreSchemeStub(options, store);
        scheme.setKeyPrefix(new LiteralExpression<String>("bad!"));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldSetKeyMapperIntoStore() throws Exception
    {
        InstanceBuilder<CacheKeyMapper> instanceBuilder = mock(InstanceBuilder.class);
        CacheKeyMapper mapper = mock(CacheKeyMapper.class);

        when(instanceBuilder.realize(resolver, classLoader, parameterList)).thenReturn(mapper);

        NoSQLBaseCacheStoreScheme scheme = new NoSQLBaseCacheStoreSchemeStub(allOptions, store);
        scheme.setCacheKeyMapperName(instanceBuilder);

        scheme.realize(resolver, classLoader, parameterList);
        assertThat(store.cacheKeyMapper, is(sameInstance(mapper)));
    }

    @SuppressWarnings("unchecked")
    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfKeyMapperNotAllowed() throws Exception
    {
        InstanceBuilder<CacheKeyMapper> instanceBuilder = mock(InstanceBuilder.class);
        EnumSet<XmlConfigParser.ConfigOption> options = EnumSet.allOf(XmlConfigParser.ConfigOption.class);
        options.remove(XmlConfigParser.ConfigOption.KEY_MAPPER);

        NoSQLBaseCacheStoreScheme scheme = new NoSQLBaseCacheStoreSchemeStub(options, store);
        scheme.setCacheKeyMapperName(instanceBuilder);
    }

    @Test
    public void shouldSetSchemaFilesIntoXmlConfig() throws Exception
    {
        String xmlValue = "testing";

        NoSQLBaseCacheStoreScheme scheme = new NoSQLBaseCacheStoreSchemeStub(allOptions, store);
        scheme.setSchemaFiles(new LiteralExpression<String>(xmlValue));

        scheme.realize(resolver, classLoader, parameterList);
        assertConfigXmlOptionSet(XmlConfigParser.OPT_SCHEMAFILES, xmlValue);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfSchemaFilesNotAllowed() throws Exception
    {
        EnumSet<XmlConfigParser.ConfigOption> options = EnumSet.allOf(XmlConfigParser.ConfigOption.class);
        options.remove(XmlConfigParser.ConfigOption.SCHEMA_FILES);

        NoSQLBaseCacheStoreScheme scheme = new NoSQLBaseCacheStoreSchemeStub(options, store);
        scheme.setSchemaFiles(new LiteralExpression<String>("bad!"));
    }

    @Test
    public void shouldSetStoreNameIntoXmlConfig() throws Exception
    {
        String xmlValue = "testing";

        NoSQLBaseCacheStoreScheme scheme = new NoSQLBaseCacheStoreSchemeStub(allOptions, store);
        scheme.setStoreName(new LiteralExpression<String>(xmlValue));

        scheme.realize(resolver, classLoader, parameterList);
        assertConfigXmlOptionSet(XmlConfigParser.OPT_STORENAME, xmlValue);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfStoreNameNotAllowed() throws Exception
    {
        EnumSet<XmlConfigParser.ConfigOption> options = EnumSet.allOf(XmlConfigParser.ConfigOption.class);
        options.remove(XmlConfigParser.ConfigOption.STORE_NAME);

        NoSQLBaseCacheStoreScheme scheme = new NoSQLBaseCacheStoreSchemeStub(options, store);
        scheme.setStoreName(new LiteralExpression<String>("bad!"));
    }

    private void assertConfigXmlOptionSet(String xmlElementName, String expectedValue)
    {
        ArgumentCaptor<XmlElement> configCaptor = ArgumentCaptor.forClass(XmlElement.class);
        verify(store).setConfig(configCaptor.capture());
        XmlElement config = configCaptor.getValue();
        assertThat(config.getSafeElement(xmlElementName).getString(), is(expectedValue));
    }

    private class NoSQLBaseCacheStoreSchemeStub extends NoSQLBaseCacheStoreScheme
    {

        private final NoSQLStoreBase store;

        private NoSQLBaseCacheStoreSchemeStub(EnumSet<XmlConfigParser.ConfigOption> allowedOptions, NoSQLStoreBase store)
        {
            super(allowedOptions);
            this.store = store;
        }

        @Override
        protected NoSQLStoreBase instantiateStore()
        {
            return store;
        }

        @Override
        public boolean realizes(Class<?> aClass, ParameterResolver parameterResolver, ClassLoader classLoader)
        {
            return true;
        }
    }

}
