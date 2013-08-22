package oracle.kv.coherence;

import com.tangosol.net.CacheFactory;
import com.tangosol.net.cache.BinaryEntryStore;
import com.tangosol.run.xml.XmlConfigurable;
import com.tangosol.run.xml.XmlElement;
import com.tangosol.util.Binary;
import com.tangosol.util.BinaryEntry;
import oracle.kv.Key;
import oracle.kv.Value;
import oracle.kv.ValueVersion;

import java.util.EnumSet;
import java.util.Set;

/**
 * @author Jonathan Knight
 */
public class NoSQLBinaryEntryStore extends NoSQLStoreBase implements BinaryEntryStore, XmlConfigurable
{
    public NoSQLBinaryEntryStore()
    {
        super(EnumSet.complementOf(EnumSet.of(XmlConfigParser.ConfigOption.AVRO_FORMAT, XmlConfigParser.ConfigOption.SCHEMA_FILES)));
    }

    @Override
    public void setConfig(XmlElement config)
    {
        super.setConfig(config);
        if (this.cacheKeyMapper == null)
        {
            this.cacheKeyMapper = new BinaryBase64CacheKeyMapper(keyPrefix);
        }
    }

    public void erase(BinaryEntry binaryEntry)
    {
        try
        {
            Key key = entryToKey(binaryEntry);
            this.kvstore.delete(key);
        }
        catch (RuntimeException e)
        {
            CacheFactory.log("erase error" + e, CacheFactory.LOG_WARN);
            throw new RuntimeException("Erase in NoSQL database failed", e);
        }
    }

    @SuppressWarnings("unchecked")
    public void eraseAll(Set binaryEntries)
    {
        for (BinaryEntry entry : (Set<BinaryEntry>) binaryEntries)
        {
            erase(entry);
        }
    }

    public void load(BinaryEntry binaryEntry)
    {
        try
        {
            Key key = entryToKey(binaryEntry);
            ValueVersion valueVersion = this.kvstore.get(key);
            if (valueVersion != null)
            {
                binaryEntry.updateBinaryValue(new Binary(valueVersion.getValue().getValue()));
            }
        }
        catch (RuntimeException e)
        {
            CacheFactory.log("load error" + e, CacheFactory.LOG_WARN);
            throw new RuntimeException("Load from NoSQL database failed", e);
        }

    }

    @SuppressWarnings("unchecked")
    public void loadAll(Set binaryEntries)
    {
        for (BinaryEntry entry : (Set<BinaryEntry>) binaryEntries)
        {
            load(entry);
        }
    }

    public void store(BinaryEntry binaryEntry)
    {
        try
        {
            Key key = entryToKey(binaryEntry);
            Value value = Value.createValue(binaryEntry.getBinaryValue().toByteArray());

            this.kvstore.put(key, value);
        }
        catch (RuntimeException e)
        {
            CacheFactory.log("store error" + e, CacheFactory.LOG_WARN);
            throw new RuntimeException("Store one K/V pair in NoSQL database failed", e);
        }
    }

    @SuppressWarnings("unchecked")
    public void storeAll(Set binaryEntries)
    {
        for (BinaryEntry entry : (Set<BinaryEntry>) binaryEntries)
        {
            store(entry);
        }
    }

    protected Key entryToKey(BinaryEntry entry)
    {
        if (this.cacheKeyMapper != null)
        {
            if (this.cacheKeyMapper instanceof BinaryEntryKeyMapper)
            {
                return ((BinaryEntryKeyMapper) this.cacheKeyMapper).mapCacheKey(entry);
            }
            return this.cacheKeyMapper.mapCacheKey(entry.getKey());
        }

        return BinaryBase64CacheKeyMapper.INSTANCE.mapCacheKey(entry);
    }

}