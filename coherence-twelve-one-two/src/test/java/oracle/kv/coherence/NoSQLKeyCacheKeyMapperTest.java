package oracle.kv.coherence;

import com.tangosol.util.BinaryEntry;
import oracle.kv.Key;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Jonathan Knight
 */
public class NoSQLKeyCacheKeyMapperTest
{
    @Test
    public void shouldMapEntryKey() throws Exception
    {
        Key key = Key.fromString("/test");
        BinaryEntry entry = mock(BinaryEntry.class);

        when(entry.getKey()).thenReturn(key);

        NoSQLKeyCacheKeyMapper mapper = new NoSQLKeyCacheKeyMapper();
        assertThat(mapper.mapCacheKey(entry), is(sameInstance(key)));
    }

    @Test
    public void shouldMapKey() throws Exception
    {
        Key key = Key.fromString("/test");

        NoSQLKeyCacheKeyMapper mapper = new NoSQLKeyCacheKeyMapper();
        assertThat(mapper.mapCacheKey(key), is(sameInstance(key)));

    }

}
