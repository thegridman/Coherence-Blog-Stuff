package com.thegridman.coherence.config;

import com.tangosol.coherence.config.builder.ServiceBuilder;
import com.tangosol.coherence.config.xml.processor.ServiceBuilderProcessor;
import com.tangosol.config.ConfigurationException;
import com.tangosol.config.xml.ProcessingContext;
import com.tangosol.run.xml.XmlElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Jonathan Knight
 */
public class ExtendedServiceBuilderProcessor<T extends ServiceBuilder> extends ServiceBuilderProcessor<T>
{
    public ExtendedServiceBuilderProcessor(Class<T> clzToRealize)
    {
        super(clzToRealize);
    }

    @SuppressWarnings("unchecked")
    @Override
    public T process(ProcessingContext context, XmlElement element) throws ConfigurationException
    {
        T builder = super.process(context, element);
        if (!(builder instanceof ExtendedServiceScheme))
        {
            return builder;
        }

        Map<String,?> map = context.processForeignElementsOf(element);
        List<ServiceExtension> extensions = new ArrayList<>(map.size());
        for (Object value : map.values())
        {
            if (value instanceof ServiceExtension)
            {
                extensions.add((ServiceExtension) value);
            }
        }
        ((ExtendedServiceScheme)builder).setExtensions(extensions);

        return builder;
    }

}
