package oracle.kv.coherence;

import com.tangosol.config.expression.ParameterResolver;

import java.util.EnumSet;

/**
 * @author Jonathan Knight
 */
public class NoSQLBinaryStoreScheme extends NoSQLBaseCacheStoreScheme
{

    public NoSQLBinaryStoreScheme()
    {
        super(EnumSet.complementOf(EnumSet.of(XmlConfigParser.ConfigOption.AVRO_FORMAT, XmlConfigParser.ConfigOption.SCHEMA_FILES)));
    }

    @Override
    protected NoSQLStoreBase instantiateStore()
    {
        return new NoSQLBinaryStore();
    }

    @Override
    public boolean realizes(Class<?> aClass, ParameterResolver resolver, ClassLoader classLoader)
    {
        return aClass.isAssignableFrom(NoSQLBinaryStore.class);
    }
}
