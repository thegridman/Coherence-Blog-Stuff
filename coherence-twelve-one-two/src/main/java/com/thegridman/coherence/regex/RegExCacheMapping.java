package com.thegridman.coherence.regex;

import com.tangosol.coherence.config.CacheMapping;
import com.tangosol.coherence.config.builder.NamedEventInterceptorBuilder;
import com.tangosol.config.annotation.Injectable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Jonathan Knight
 */
public class RegExCacheMapping extends CacheMapping
{
    private Pattern pattern;

    public RegExCacheMapping(String regEx, String schemeName)
    {
        super(regEx, schemeName);
        pattern = Pattern.compile(regEx);
    }

    @Override
    public boolean isForCacheName(String sCacheName)
    {
        Matcher matcher = pattern.matcher(sCacheName);
        return matcher.matches();
    }

    @Override
    public boolean usesWildcard()
    {
        return true;
    }

    @Override
    @Injectable("interceptors")
    public void setEventInterceptorBuilders(List<NamedEventInterceptorBuilder> listBuilders)
    {
        List<NamedEventInterceptorBuilder> regExBuilders = new ArrayList<>(listBuilders.size());
        for (NamedEventInterceptorBuilder builder : listBuilders)
        {
            regExBuilders.add(new RegExEventInterceptorBuilder(builder));
        }
        super.setEventInterceptorBuilders(regExBuilders);
    }
}
