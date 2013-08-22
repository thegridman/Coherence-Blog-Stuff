package oracle.kv.coherence;

import com.tangosol.config.xml.ElementProcessor;
import com.tangosol.config.xml.ProcessingContext;
import com.tangosol.run.xml.SimpleElement;
import com.tangosol.run.xml.XmlElement;
import org.junit.Test;

import static org.mockito.Matchers.isA;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * @author Jonathan Knight
 */
public class NoSQLNamespaceHandlerTest
{
    @Test
    public void shouldInjectNoSQLBinaryStoreScheme() throws Exception
    {
        XmlElement element = new SimpleElement(NoSQLNamespaceHandler.XMLTAG_BINARY_CACHE_STORE);
        ProcessingContext context = mock(ProcessingContext.class);

        NoSQLNamespaceHandler handler = new NoSQLNamespaceHandler();
        ElementProcessor<?> elementProcessor = handler.getElementProcessor(NoSQLNamespaceHandler.XMLTAG_BINARY_CACHE_STORE);
        elementProcessor.process(context, element);

        verify(context).inject(isA(NoSQLBinaryStoreScheme.class), same(element));
    }

    @Test
    public void shouldInjectNoSQLAvroCacheStoreScheme() throws Exception
    {
        XmlElement element = new SimpleElement(NoSQLNamespaceHandler.XMLTAG_AVRO_CACHE_STORE);
        ProcessingContext context = mock(ProcessingContext.class);

        NoSQLNamespaceHandler handler = new NoSQLNamespaceHandler();
        ElementProcessor<?> elementProcessor = handler.getElementProcessor(NoSQLNamespaceHandler.XMLTAG_AVRO_CACHE_STORE);
        elementProcessor.process(context, element);

        verify(context).inject(isA(NoSQLAvroCacheStoreScheme.class), same(element));
    }
}
