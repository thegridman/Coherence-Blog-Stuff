package com.thegridman.coherence.regex;

import com.tangosol.net.DefaultConfigurableCacheFactory;
import com.tangosol.run.xml.XmlElement;
import com.tangosol.run.xml.XmlHelper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Jonathan Knight
 */
public class RegExConfigurableCacheFactory extends DefaultConfigurableCacheFactory
{
    public static final String NAMESPACE_URI = "http://xmlns.oracle.com/coherence/regex-coherence-cache-config";

    private Map<XmlElement,Pattern> cacheMappingPatterns;
    private Map<String,XmlElement> exactCacheMappings;
    private String namespacePrefix;

    public RegExConfigurableCacheFactory()
    {
    }

    public RegExConfigurableCacheFactory(String sPath)
    {
        super(sPath);
    }

    public RegExConfigurableCacheFactory(String sPath, ClassLoader loader)
    {
        super(sPath, loader);
    }

    public RegExConfigurableCacheFactory(XmlElement xmlConfig)
    {
        super(xmlConfig);
    }

    public RegExConfigurableCacheFactory(XmlElement xmlConfig, ClassLoader loader)
    {
        super(xmlConfig, loader);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setConfig(XmlElement xmlConfig)
    {
        super.setConfig(xmlConfig);
        namespacePrefix = XmlHelper.getNamespacePrefix(xmlConfig, NAMESPACE_URI);
        cacheMappingPatterns = new LinkedHashMap<>();
        Iterator<XmlElement> regExMappingIterator =
                xmlConfig.getSafeElement("caching-scheme-mapping")
                        .getElements(namespacePrefix + ":regex-cache-mapping");

        while (regExMappingIterator.hasNext())
        {
            XmlElement mappingElement = regExMappingIterator.next();
            String regEx = mappingElement.getSafeElement(namespacePrefix + ":cache-regex").getString();
            Pattern pattern = Pattern.compile(regEx);
            cacheMappingPatterns.put(mappingElement, pattern);
        }

        exactCacheMappings = new LinkedHashMap<>();
        Iterator<XmlElement> mappingIterator =
                xmlConfig.getSafeElement("caching-scheme-mapping")
                    .getElements("cache-mapping");

        while (mappingIterator.hasNext())
        {
            XmlElement mappingElement = mappingIterator.next();
            String cacheName = mappingElement.getSafeElement("cache-name").getString();
            exactCacheMappings.put(cacheName, mappingElement);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public CacheInfo findSchemeMapping(String cacheName)
    {
        XmlElement match = null;
        String schemeName = null;
        if (exactCacheMappings.containsKey(cacheName))
        {
            match = exactCacheMappings.get(cacheName);
            schemeName = match.getSafeElement("scheme-name").getString();
        }
        else
        {
            for (Map.Entry<XmlElement,Pattern> mappingEntry : cacheMappingPatterns.entrySet())
            {
                Matcher matcher = mappingEntry.getValue().matcher(cacheName);
                if (matcher.matches())
                {
                    match = mappingEntry.getKey();
                    schemeName = match.getSafeElement(namespacePrefix + ":scheme-name").getString();
                }
            }
        }

        if (match == null)
        {
            throw new IllegalArgumentException("No scheme for cache: \"" + cacheName + '"');
        }

        Map initParamMap = new HashMap();
        Iterator<XmlElement> initParamIterator = match.getSafeElement("init-params").getElements("init-param");
        while(initParamIterator.hasNext())
        {
            XmlElement initParamElement = initParamIterator.next();
            String paramName = initParamElement.getSafeElement("param-name").getString();
            if (paramName.length() > 0)
            {
                String paramValue = initParamElement.getSafeElement("param-value").getString();
                initParamMap.put(paramName, paramValue);
            }
        }

        return new CacheInfo(cacheName, schemeName, initParamMap);
    }

}
