package oracle.kv.coherence;

import org.junit.Test;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author Jonathan Knight
 */
public class NoSQLBinaryStoreSchemeTest
{
    @Test
    public void shouldRealizeCorrectClass() throws Exception
    {
        NoSQLBinaryStoreScheme scheme = new NoSQLBinaryStoreScheme();
        assertThat(scheme.realizes(NoSQLBinaryStore.class, null, null), is(true));
    }

    @Test
    public void shouldInstantiateNoSQLAvroCacheStore() throws Exception
    {
        NoSQLBinaryStoreScheme scheme = new NoSQLBinaryStoreScheme();
        assertThat(scheme.instantiateStore(), is(instanceOf(NoSQLBinaryStore.class)));
    }

}
