package com.thegridman.coherence.regex;

import com.tangosol.coherence.config.CacheMapping;
import com.tangosol.config.ConfigurationException;
import com.tangosol.config.xml.AbstractNamespaceHandler;
import com.tangosol.config.xml.ElementProcessor;
import com.tangosol.config.xml.ProcessingContext;
import com.tangosol.config.xml.XmlSimpleName;
import com.tangosol.run.xml.XmlElement;

/**
 * @author Jonathan Knight
 */
public class RegExMappingNamespaceHandler extends AbstractNamespaceHandler
{
    public static final String XMLTAG_CACHE_MAPPING = "regex-cache-mapping";

    public RegExMappingNamespaceHandler()
    {
    }

    @XmlSimpleName(value = XMLTAG_CACHE_MAPPING)
    public static class CacheMappingElementProcessor implements ElementProcessor<RegExCacheMapping>
    {
        @SuppressWarnings("unchecked")
        @Override
        public RegExCacheMapping process(ProcessingContext context, XmlElement element) throws ConfigurationException
        {
            String cacheNamePattern = context.getMandatoryProperty("cache-regex", String.class, element);
            String schemeName = context.getMandatoryProperty("scheme-name", String.class, element);
            RegExCacheMapping mapping = new RegExCacheMapping(cacheNamePattern, schemeName);

            context.inject(mapping, element);

            XmlElement copy = (XmlElement) element.clone();
            copy.setName(element.getQualifiedName().getLocalName());
            context.inject(mapping, copy);
            context.addCookie(CacheMapping.class, mapping);
            return mapping;
        }
    }

}
