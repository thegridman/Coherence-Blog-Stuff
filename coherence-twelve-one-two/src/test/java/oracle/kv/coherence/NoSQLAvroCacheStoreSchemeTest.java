package oracle.kv.coherence;

import org.junit.Test;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author Jonathan Knight
 */
public class NoSQLAvroCacheStoreSchemeTest
{
    @Test
    public void shouldRealizeCorrectClass() throws Exception
    {
        NoSQLAvroCacheStoreScheme scheme = new NoSQLAvroCacheStoreScheme();
        assertThat(scheme.realizes(NoSQLAvroCacheStore.class, null, null), is(true));
    }

    @Test
    public void shouldInstantiateNoSQLAvroCacheStore() throws Exception
    {
        NoSQLAvroCacheStoreScheme scheme = new NoSQLAvroCacheStoreScheme();
        assertThat(scheme.instantiateStore(), is(instanceOf(NoSQLAvroCacheStore.class)));
    }

}
