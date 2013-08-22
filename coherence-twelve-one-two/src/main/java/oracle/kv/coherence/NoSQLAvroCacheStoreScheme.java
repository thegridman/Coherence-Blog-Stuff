package oracle.kv.coherence;

import com.tangosol.config.expression.ParameterResolver;

import java.util.EnumSet;

/**
 * @author Jonathan Knight
 */
public class NoSQLAvroCacheStoreScheme extends NoSQLBaseCacheStoreScheme
{

    public NoSQLAvroCacheStoreScheme()
    {
        super(EnumSet.allOf(XmlConfigParser.ConfigOption.class));
    }

    @Override
    protected NoSQLStoreBase instantiateStore()
    {
        return new NoSQLAvroCacheStore();
    }

    @Override
    public boolean realizes(Class<?> aClass, ParameterResolver resolver, ClassLoader classLoader)
    {
        return aClass.isAssignableFrom(NoSQLAvroCacheStore.class);
    }
}
