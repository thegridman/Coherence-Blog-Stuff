package oracle.kv.coherence;

import com.tangosol.util.BinaryEntry;
import oracle.kv.Key;

/**
 * This class is a {@link CacheKeyMapper} implementation for caches
 * that use a NoSQL Key class as the cache key.
 *
 * @author Jonathan Knight
 */
public class NoSQLKeyCacheKeyMapper implements BinaryEntryKeyMapper
{
    public NoSQLKeyCacheKeyMapper()
    {
    }

    @Override
    public Key mapCacheKey(BinaryEntry entry)
    {
        return (Key) entry.getKey();
    }

    @Override
    public Key mapCacheKey(Object cacheKey)
    {
        return (Key) cacheKey;
    }
}
