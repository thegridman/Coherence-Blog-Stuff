package oracle.kv.coherence;

import com.tangosol.util.Binary;
import com.tangosol.util.BinaryEntry;
import oracle.kv.KVStore;
import oracle.kv.Key;
import oracle.kv.Value;
import oracle.kv.ValueVersion;
import oracle.kv.Version;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Jonathan Knight
 */
public class NoSQLBinaryEntryStoreTest
{
    private BinaryEntry entryOne;
    private BinaryEntry entryTwo;
    private KVStore kvStore;
    private BinaryEntryKeyMapper mapper;
    private Key kvKeyOne;
    private Key kvKeyTwo;
    private Value valueOne;
    private ValueVersion valueVersionOne;
    private Value valueTwo;
    private ValueVersion valueVersionTwo;
    private Binary binaryKeyOne;
    private Binary binaryValueOne;
    private Binary binaryValueTwo;

    @Before
    public void setup()
    {
        binaryKeyOne = new Binary("key-one".getBytes());
        binaryValueOne = new Binary("value-one".getBytes());
        binaryValueTwo = new Binary("value-two".getBytes());
        kvKeyOne = Key.fromString("/Test1");
        kvKeyTwo = Key.fromString("/Test2");
        valueOne = Value.createValue(binaryValueOne.toByteArray());
        valueVersionOne = new ValueVersion(valueOne, new Version(UUID.randomUUID(), 1L));
        valueTwo = Value.createValue(binaryValueTwo.toByteArray());
        valueVersionTwo = new ValueVersion(valueTwo, new Version(UUID.randomUUID(), 1L));

        entryOne = mock(BinaryEntry.class);
        entryTwo = mock(BinaryEntry.class);
        kvStore = mock(KVStore.class);
        mapper = mock(BinaryEntryKeyMapper.class);

        when(mapper.mapCacheKey(entryOne)).thenReturn(kvKeyOne);
        when(mapper.mapCacheKey(entryTwo)).thenReturn(kvKeyTwo);
        when(entryOne.getKey()).thenReturn("Key-1");
        when(entryOne.getBinaryKey()).thenReturn(binaryKeyOne);
        when(entryOne.getBinaryValue()).thenReturn(binaryValueOne);
        when(entryTwo.getBinaryValue()).thenReturn(binaryValueTwo);
        when(kvStore.get(kvKeyOne)).thenReturn(valueVersionOne);
        when(kvStore.get(kvKeyTwo)).thenReturn(valueVersionTwo);
    }

    @Test
    public void shouldDeleteFromKvStore() throws Exception
    {
        NoSQLBinaryEntryStore store = new NoSQLBinaryEntryStore();
        store.cacheKeyMapper = mapper;
        store.kvstore = kvStore;

        store.erase(entryOne);

        verify(kvStore).delete(kvKeyOne);
    }

    @Test
    public void shouldDeleteAllFromKvStore() throws Exception
    {
        NoSQLBinaryEntryStore store = new NoSQLBinaryEntryStore();
        store.cacheKeyMapper = mapper;
        store.kvstore = kvStore;

        store.eraseAll(new HashSet<BinaryEntry>(Arrays.asList(entryOne, entryTwo)));

        verify(kvStore).delete(kvKeyOne);
        verify(kvStore).delete(kvKeyTwo);
    }

    @Test
    public void shouldLoadFromKvStore() throws Exception
    {
        NoSQLBinaryEntryStore store = new NoSQLBinaryEntryStore();
        store.cacheKeyMapper = mapper;
        store.kvstore = kvStore;

        store.load(entryOne);

        ArgumentCaptor<Binary> binaryArgumentCaptor = ArgumentCaptor.forClass(Binary.class);
        verify(entryOne).updateBinaryValue(binaryArgumentCaptor.capture());
        Binary binaryValue = binaryArgumentCaptor.getValue();
        assertThat(binaryValue.toByteArray(), is(valueOne.getValue()));
    }

    @Test
    public void shouldLoadAllFromKvStore() throws Exception
    {
        NoSQLBinaryEntryStore store = new NoSQLBinaryEntryStore();
        store.cacheKeyMapper = mapper;
        store.kvstore = kvStore;

        store.loadAll(new HashSet<BinaryEntry>(Arrays.asList(entryOne, entryTwo)));

        ArgumentCaptor<Binary> binaryArgumentCaptorOne = ArgumentCaptor.forClass(Binary.class);
        verify(entryOne).updateBinaryValue(binaryArgumentCaptorOne.capture());
        Binary binaryValue = binaryArgumentCaptorOne.getValue();
        assertThat(binaryValue.toByteArray(), is(valueOne.getValue()));

        ArgumentCaptor<Binary> binaryArgumentCaptorTwo = ArgumentCaptor.forClass(Binary.class);
        verify(entryTwo).updateBinaryValue(binaryArgumentCaptorTwo.capture());
        binaryValue = binaryArgumentCaptorTwo.getValue();
        assertThat(binaryValue.toByteArray(), is(valueTwo.getValue()));
    }

    @Test
    public void shouldStoreEntryInKvStore() throws Exception
    {
        NoSQLBinaryEntryStore store = new NoSQLBinaryEntryStore();
        store.cacheKeyMapper = mapper;
        store.kvstore = kvStore;

        store.store(entryOne);

        ArgumentCaptor<Value> valueOneArgumentCaptor = ArgumentCaptor.forClass(Value.class);
        verify(kvStore).put(eq(kvKeyOne), valueOneArgumentCaptor.capture());
        Value value = valueOneArgumentCaptor.getValue();
        assertThat(value.toByteArray(), is(valueOne.toByteArray()));
    }

    @Test
    public void shouldStoreAllInKvStore() throws Exception
    {
        NoSQLBinaryEntryStore store = new NoSQLBinaryEntryStore();
        store.cacheKeyMapper = mapper;
        store.kvstore = kvStore;

        store.storeAll(new HashSet<BinaryEntry>(Arrays.asList(entryOne, entryTwo)));

        ArgumentCaptor<Value> valueOneArgumentCaptor = ArgumentCaptor.forClass(Value.class);
        verify(kvStore).put(eq(kvKeyOne), valueOneArgumentCaptor.capture());
        Value value = valueOneArgumentCaptor.getValue();
        assertThat(value.toByteArray(), is(valueOne.toByteArray()));

        ArgumentCaptor<Value> valueTwoArgumentCaptor = ArgumentCaptor.forClass(Value.class);
        verify(kvStore).put(eq(kvKeyTwo), valueTwoArgumentCaptor.capture());
        value = valueTwoArgumentCaptor.getValue();
        assertThat(value.toByteArray(), is(valueTwo.toByteArray()));
    }

    @Test
    public void shouldUseEntryMapperToConvertKey() throws Exception
    {
        NoSQLBinaryEntryStore store = new NoSQLBinaryEntryStore();
        store.cacheKeyMapper = mapper;
        Key key = store.entryToKey(entryOne);

        assertThat(key, is(kvKeyOne));
        verify(mapper).mapCacheKey(entryOne);
    }

    @Test
    public void shouldUseMapperToConvertKey() throws Exception
    {
        CacheKeyMapper cacheKeyMapper = mock(CacheKeyMapper.class);
        when(cacheKeyMapper.mapCacheKey("Key-1")).thenReturn(kvKeyOne);

        NoSQLBinaryEntryStore store = new NoSQLBinaryEntryStore();
        store.cacheKeyMapper = cacheKeyMapper;
        Key key = store.entryToKey(entryOne);

        assertThat(key, is(kvKeyOne));
        verify(cacheKeyMapper).mapCacheKey("Key-1");
    }

    @Test
    public void shouldDefaultToBase64MapperIfMapperFieldIsNull() throws Exception
    {
        NoSQLBinaryEntryStore store = new NoSQLBinaryEntryStore();
        store.cacheKeyMapper = null;

        Key key = store.entryToKey(entryOne);
        Key expected = BinaryBase64CacheKeyMapper.INSTANCE.mapCacheKey(entryOne);
        assertThat(key, is(expected));
    }


}
