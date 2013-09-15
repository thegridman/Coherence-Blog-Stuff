package com.thegridman.coherence.cqc;

import com.tangosol.coherence.component.util.collections.wrapperMap.WrapperNamedCache;
import com.tangosol.net.NamedCache;
import com.tangosol.net.cache.ContinuousQueryCache;
import com.tangosol.util.Filter;
import com.tangosol.util.MapEventTransformer;
import com.tangosol.util.MapListener;
import com.tangosol.util.ValueExtractor;
import com.tangosol.util.filter.MapEventFilter;
import com.tangosol.util.filter.MapEventTransformerFilter;
import com.tangosol.util.transformer.SemiLiteEventTransformer;
import com.thegridman.coherence.events.PofExtractorEventTransformer;

/**
 * @author Jonathan Knight
 */
public class ContinuousQueryCacheWrapper extends WrapperNamedCache
{

    public static class CQC extends ContinuousQueryCache
    {
        private final ValueExtractor transformer;

        public CQC(NamedCache cache, Filter filter)
        {
            this(cache, filter, true, null, null);
        }

        public CQC(NamedCache cache, Filter filter, boolean fCacheValues)
        {
            this(cache, filter, fCacheValues, null, null);
        }

        public CQC(NamedCache cache, Filter filter, MapListener listener)
        {
            this(cache, filter, false, listener, null);
        }

        public CQC(NamedCache cache, Filter filter, ValueExtractor transformer)
        {
            this(cache, filter, true, null, transformer);
        }

        protected CQC(NamedCache cache, Filter filter, boolean fCacheValues, MapListener listener, ValueExtractor transformer)
        {
            super(cache, filter, fCacheValues, listener, transformer);
            this.transformer = transformer;
        }

        protected Filter createTransformerFilter(MapEventFilter filterAdd)
        {
            MapEventTransformer eventTransformer;
            if (this.transformer == null)
            {
                eventTransformer = SemiLiteEventTransformer.INSTANCE;
            }
            else
            {
                eventTransformer = new PofExtractorEventTransformer(null, this.transformer);
            }
            return new MapEventTransformerFilter(filterAdd, eventTransformer);
        }

    }
}
