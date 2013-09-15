package com.thegridman.coherence.config;

import com.tangosol.config.ConfigurationException;
import com.tangosol.config.xml.ElementProcessor;
import com.tangosol.config.xml.ProcessingContext;
import com.tangosol.run.xml.XmlElement;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jonathan Knight
 */
public class ListOfInstancesProcessor<R extends ListOfInstancesProcessor.ListOfInstancesScheme,T> implements ElementProcessor<R>
{
    private final Class<R> classToRealize;
    private final Class<T> type;

    public ListOfInstancesProcessor(Class<R> classToRealize, Class<T> type)
    {
        this.classToRealize = classToRealize;
        this.type = type;
    }

    @SuppressWarnings("unchecked")
    @Override
    public R process(ProcessingContext context, XmlElement element) throws ConfigurationException
    {
        List<XmlElement> children = (List<XmlElement>) element.getElementList();
        ArrayList<T> instances = new ArrayList<>(children.size());
        for (XmlElement child : children)
        {
            try
            {
                T value = (T) context.processElement(child);
                if (!type.isAssignableFrom(value.getClass()))
                {
                    throw new ClassCastException();
                }
                instances.add(value);
            }
            catch (ClassCastException e)
            {
                throw new ConfigurationException("Element " + child.getName()
                                                 + " does not produce an instance of "
                                                 + type,
                                                 "Check the configuration", e);
            }
        }

        R scheme;
        try
        {
            scheme = classToRealize.newInstance();
            scheme.setInstances(instances);
        }
        catch (Exception e)
        {
            throw new ConfigurationException("Error creating class " + classToRealize,
                                             "Make sure class " + classToRealize +
                                             " is a valid class with public no-arg constructor", e);
        }
        return scheme;
    }

    public static interface ListOfInstancesScheme<T>
    {
        void setInstances(List<T> instances);
    }
}
