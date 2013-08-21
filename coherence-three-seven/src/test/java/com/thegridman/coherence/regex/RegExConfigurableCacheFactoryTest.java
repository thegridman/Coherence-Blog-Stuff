package com.thegridman.coherence.regex;

import com.tangosol.net.DefaultConfigurableCacheFactory;
import com.tangosol.run.xml.XmlElement;
import com.tangosol.run.xml.XmlHelper;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author Jonathan Knight §
 */
public class RegExConfigurableCacheFactoryTest
{

    private static XmlElement xmlConfig;

    @BeforeClass
    public static void loadXml()
    {
        xmlConfig = XmlHelper.loadFileOrResource("RegExConfigurableCacheFactoryTest.xml", null);
    }

    @Test
    public void shouldMatchExact() throws Exception
    {
        RegExConfigurableCacheFactory cacheFactory = new RegExConfigurableCacheFactory(xmlConfig);
        DefaultConfigurableCacheFactory.CacheInfo cacheInfo = cacheFactory.findSchemeMapping("dist-test");
        assertThat(cacheInfo.getSchemeName(), is("my-distributed-scheme"));
    }

    @Test
    public void shouldMatchSuffix() throws Exception
    {
        RegExConfigurableCacheFactory cacheFactory = new RegExConfigurableCacheFactory(xmlConfig);
        DefaultConfigurableCacheFactory.CacheInfo cacheInfo = cacheFactory.findSchemeMapping("dist-test-2");
        assertThat(cacheInfo.getSchemeName(), is("regex-scheme-one"));
    }

    @Test
    public void shouldMatchPrefix() throws Exception
    {
        RegExConfigurableCacheFactory cacheFactory = new RegExConfigurableCacheFactory(xmlConfig);
        DefaultConfigurableCacheFactory.CacheInfo cacheInfo = cacheFactory.findSchemeMapping("jk-test");
        assertThat(cacheInfo.getSchemeName(), is("regex-scheme-two"));
    }

    @Test
    public void shouldMatchMiddle() throws Exception
    {
        RegExConfigurableCacheFactory cacheFactory = new RegExConfigurableCacheFactory(xmlConfig);
        DefaultConfigurableCacheFactory.CacheInfo cacheInfo = cacheFactory.findSchemeMapping("dist-jk-test");
        assertThat(cacheInfo.getSchemeName(), is("regex-scheme-three"));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldSetInitParams() throws Exception
    {
        RegExConfigurableCacheFactory cacheFactory = new RegExConfigurableCacheFactory(xmlConfig);
        DefaultConfigurableCacheFactory.CacheInfo cacheInfo = cacheFactory.findSchemeMapping("dist-test-2");
        Map<String,String> attributes = cacheInfo.getAttributes();
        assertThat(attributes.size(), is(2));
        assertThat(attributes.get("expiry"), is("1d"));
        assertThat(attributes.get("test-param"), is("test-value"));
    }

}
