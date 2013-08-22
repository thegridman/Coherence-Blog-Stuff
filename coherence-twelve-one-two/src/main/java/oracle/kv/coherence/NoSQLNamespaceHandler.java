package oracle.kv.coherence;

import com.tangosol.config.ConfigurationException;
import com.tangosol.config.xml.AbstractNamespaceHandler;
import com.tangosol.config.xml.ElementProcessor;
import com.tangosol.config.xml.ProcessingContext;
import com.tangosol.config.xml.XmlSimpleName;
import com.tangosol.run.xml.XmlElement;

/**
 * @author Jonathan Knight
 */
public class NoSQLNamespaceHandler extends AbstractNamespaceHandler
{

    public static final String XMLTAG_BINARY_CACHE_STORE = "binary-cache-store";
    public static final String XMLTAG_AVRO_CACHE_STORE = "avro-cache-store";

    public NoSQLNamespaceHandler()
    {
    }

    @XmlSimpleName(value = XMLTAG_BINARY_CACHE_STORE)
    public static class NoSQLBinaryStoreElementProcessor implements ElementProcessor<NoSQLBinaryStoreScheme>
    {

        @Override
        public NoSQLBinaryStoreScheme process(ProcessingContext context, XmlElement element) throws ConfigurationException
        {
            return context.inject(new NoSQLBinaryStoreScheme(), element);
        }

    }

    @XmlSimpleName(value = XMLTAG_AVRO_CACHE_STORE)
    public static class NoSQLAvroCacheStoreElementProcessor implements ElementProcessor<NoSQLAvroCacheStoreScheme>
    {
        @Override
        public NoSQLAvroCacheStoreScheme process(ProcessingContext context, XmlElement element) throws ConfigurationException
        {
            return context.inject(new NoSQLAvroCacheStoreScheme(), element);
        }
    }
}
