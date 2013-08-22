package oracle.kv.coherence;

import com.tangosol.util.BinaryEntry;
import oracle.kv.Key;

/**
 * @author Jonathan Knight
 */
public interface BinaryEntryKeyMapper extends CacheKeyMapper
{
    Key mapCacheKey(BinaryEntry entry);
}
