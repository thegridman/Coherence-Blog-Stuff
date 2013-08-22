package oracle.kv.coherence;

import com.tangosol.coherence.config.ParameterList;
import com.tangosol.coherence.config.builder.InstanceBuilder;
import com.tangosol.coherence.config.builder.ParameterizedBuilder;
import com.tangosol.coherence.config.unit.Millis;
import com.tangosol.config.annotation.Injectable;
import com.tangosol.config.expression.Expression;
import com.tangosol.config.expression.ParameterResolver;
import com.tangosol.run.xml.SimpleElement;
import com.tangosol.run.xml.XmlElement;

import java.util.EnumSet;

/**
 * Coherence 12.1.2 configuration scheme for configuring and realizing
 * concrete instances of NoSQLStoreBase sub-classes
 *
 * @author Jonathan Knight
 */
public abstract class NoSQLBaseCacheStoreScheme implements ParameterizedBuilder<NoSQLStoreBase>, ParameterizedBuilder.ReflectionSupport
{

    /**
     * enum that controls which configuration options are allowed
     */
    private final EnumSet<XmlConfigParser.ConfigOption> allowedOptions;

    private Expression<String> keyPrefixExpression;
    private Expression<String> avroFormatExpression;
    private Expression<String> schemaFilesExpression;
    private Expression<String> storeNameExpression;
    private Expression<String> helperHostsExpression;
    private Expression<String> consistencyExpression;
    private Expression<Millis> consistencyTimeLagExpression;
    private Expression<Millis> consistencyTimeoutExpression;
    private Expression<String> masterSyncPolicyExpression;
    private Expression<String> replicaSyncPolicyExpression;
    private Expression<String> replicaAckPolicyExpression;
    private InstanceBuilder<CacheKeyMapper> keyMapperBuilder;

    protected NoSQLBaseCacheStoreScheme(EnumSet<XmlConfigParser.ConfigOption> allowedOptions)
    {
        this.allowedOptions = allowedOptions;
    }

    /**
     * Realize and configure a concrete sub-class of NoSQLStoreBase
     *
     * @param resolver
     * @param classLoader
     * @param parameterList
     * @return a concrete sub-class of NoSQLStoreBase
     */
    @Override
    public NoSQLStoreBase realize(ParameterResolver resolver, ClassLoader classLoader, ParameterList parameterList)
    {
        XmlElement configXml = new SimpleElement("config");
        addElementIfNotNull(configXml, XmlConfigParser.OPT_STORENAME, getStoreName(resolver));
        addElementIfNotNull(configXml, XmlConfigParser.OPT_HELPERHOSTS, getHelperHosts(resolver));
        addElementIfNotNull(configXml, XmlConfigParser.OPT_CONSISTENCY, getConsistency(resolver));
        addElementIfNotNull(configXml, XmlConfigParser.OPT_CONSISTENCYTIMELAG, getConsistencyTimeLag(resolver));
        addElementIfNotNull(configXml, XmlConfigParser.OPT_CONSISTENCYTIMEOUT, getConsistencyTimeout(resolver));
        addElementIfNotNull(configXml, XmlConfigParser.OPT_DURABILITYMASTERSYNC, getDurabilityMasterSync(resolver));
        addElementIfNotNull(configXml, XmlConfigParser.OPT_DURABILITYREPLICASYNC, getDurabilityReplicaSync(resolver));
        addElementIfNotNull(configXml, XmlConfigParser.OPT_DURABILITYREPLICAACK, getDurabilityReplicaAck(resolver));
        addElementIfNotNull(configXml, XmlConfigParser.OPT_AVROFORMAT, getAvroFormat(resolver));
        addElementIfNotNull(configXml, XmlConfigParser.OPT_SCHEMAFILES, getSchemaFiles(resolver));
        addElementIfNotNull(configXml, XmlConfigParser.OPT_KEYPREFIX, getKeyPrefix(resolver));

        NoSQLStoreBase store = instantiateStore();
        store.cacheKeyMapper = getKeyMapper(resolver, classLoader, parameterList);
        store.setConfig(configXml);

        return store;
    }

    @Override
    public abstract boolean realizes(Class<?> paramClass, ParameterResolver paramParameterResolver, ClassLoader paramClassLoader);

    /**
     * Implemented by sub-classes to realize a concrete sub-class of NoSQLStoreBase.
     *
     * @return a concrete sub-class of NoSQLStoreBase
     */
    protected abstract NoSQLStoreBase instantiateStore();

    private void addElementIfNotNull(XmlElement parent, String elementName, Object value)
    {
        if (value == null)
        {
            return;
        }

        parent.addElement(elementName).setString(String.valueOf(value));
    }

    @Injectable("store-name")
    public void setStoreName(Expression<String> expression)
    {
        assertAllowedOption("store-name", XmlConfigParser.ConfigOption.STORE_NAME);
        storeNameExpression = expression;
    }

    public String getStoreName(ParameterResolver resolver)
    {
        return (storeNameExpression != null) ? storeNameExpression.evaluate(resolver) : null;
    }

    @Injectable("helper-hosts")
    public void setHelperHosts(Expression<String> expression)
    {
        assertAllowedOption("helper-hosts", XmlConfigParser.ConfigOption.HELPER_HOSTS);
        helperHostsExpression = expression;
    }

    public String getHelperHosts(ParameterResolver resolver)
    {
        return (helperHostsExpression == null) ? null : helperHostsExpression.evaluate(resolver);
    }

    @Injectable("consistency")
    public void setConsistency(Expression<String> expression)
    {
        assertAllowedOption("store-name", XmlConfigParser.ConfigOption.CONSISTENCY);
        consistencyExpression = expression;
    }

    public String getConsistency(ParameterResolver resolver)
    {
        return (consistencyExpression == null) ? null : consistencyExpression.evaluate(resolver);
    }

    @Injectable("consistency-time-lag")
    public void setConsistencyTimeLag(Expression<Millis> expression)
    {
        assertAllowedOption("consistency-time-lag", XmlConfigParser.ConfigOption.CONSISTENCY);
        consistencyTimeLagExpression = expression;
    }

    public Long getConsistencyTimeLag(ParameterResolver resolver)
    {
        return (consistencyTimeLagExpression == null) ? null : consistencyTimeLagExpression.evaluate(resolver).getNanos();
    }

    @Injectable("consistency-timeout")
    public void setConsistencyTimeout(Expression<Millis> expression)
    {
        assertAllowedOption("consistency-timeout", XmlConfigParser.ConfigOption.CONSISTENCY);
        consistencyTimeoutExpression = expression;
    }

    public Long getConsistencyTimeout(ParameterResolver resolver)
    {
        return (consistencyTimeoutExpression == null) ? null : consistencyTimeoutExpression.evaluate(resolver).getNanos();
    }

    @Injectable("durability-master-sync")
    public void setDurabilityMasterSync(Expression<String> expression)
    {
        assertAllowedOption("durability-master-sync", XmlConfigParser.ConfigOption.DURABILITY);
        masterSyncPolicyExpression = expression;
    }

    public String getDurabilityMasterSync(ParameterResolver resolver)
    {
        return (masterSyncPolicyExpression == null) ? null : masterSyncPolicyExpression.evaluate(resolver);
    }

    @Injectable("durability-replica-sync")
    public void setDurabilityReplicaSync(Expression<String> expression)
    {
        assertAllowedOption("durability-replica-sync", XmlConfigParser.ConfigOption.DURABILITY);
        replicaSyncPolicyExpression = expression;
    }

    public String getDurabilityReplicaSync(ParameterResolver resolver)
    {
        return (replicaSyncPolicyExpression == null) ? null : replicaSyncPolicyExpression.evaluate(resolver);
    }

    @Injectable("durability-replica-ack")
    public void setDurabilityReplicaAck(Expression<String> expression)
    {
        assertAllowedOption("durability-replica-ack", XmlConfigParser.ConfigOption.DURABILITY);
        replicaAckPolicyExpression = expression;
    }

    public String getDurabilityReplicaAck(ParameterResolver resolver)
    {
        return (replicaAckPolicyExpression == null) ? null : replicaAckPolicyExpression.evaluate(resolver);
    }

    @Injectable("key-prefix")
    public void setKeyPrefix(Expression<String> keyPrefixExpression)
    {
        assertAllowedOption("key-prefix", XmlConfigParser.ConfigOption.KEY_PREFIX);
        this.keyPrefixExpression = keyPrefixExpression;
    }

    public String getKeyPrefix(ParameterResolver resolver)
    {
        return (keyPrefixExpression == null) ? null : keyPrefixExpression.evaluate(resolver);
    }

    @Injectable("key-mapper")
    public void setCacheKeyMapperName(InstanceBuilder<CacheKeyMapper> keyMapperBuilder)
    {
        assertAllowedOption("key-mapper", XmlConfigParser.ConfigOption.KEY_MAPPER);
        this.keyMapperBuilder = keyMapperBuilder;
    }

    public CacheKeyMapper getKeyMapper(ParameterResolver resolver, ClassLoader classLoader, ParameterList parameterList)
    {
        if (keyMapperBuilder != null)
        {
            return keyMapperBuilder.realize(resolver, classLoader, parameterList);
        }

        return null;
    }

    @Injectable("avro-format")
    public void setAvroFormat(Expression<String> expression)
    {
        assertAllowedOption("avro-format", XmlConfigParser.ConfigOption.AVRO_FORMAT);
        avroFormatExpression = expression;
    }

    public String getAvroFormat(ParameterResolver resolver)
    {
        return (avroFormatExpression == null) ? null : avroFormatExpression.evaluate(resolver);
    }

    @Injectable("schema-files")
    public void setSchemaFiles(Expression<String> expression)
    {
        assertAllowedOption("schema-files", XmlConfigParser.ConfigOption.SCHEMA_FILES);
        schemaFilesExpression = expression;
    }

    public String getSchemaFiles(ParameterResolver resolver)
    {
        return (schemaFilesExpression == null) ? null : schemaFilesExpression.evaluate(resolver);
    }

    private void assertAllowedOption(String optionName, XmlConfigParser.ConfigOption option)
    {
        if (!allowedOptions.contains(option))
        {
            throw new IllegalArgumentException("Error: the <" + optionName + "> value is invalid for " + getClass().getSimpleName());
        }
    }

}
