package com.thegridman.coherence.config;

/**
 * @author Jonathan Knight
 */
public class NamedResource
{
    private String name;
    private Class resourceClass;
    private Object resource;

    public NamedResource(String name, Class resourceClass, Object resource)
    {
        this.name = name;
        this.resource = resource;
        this.resourceClass = (resourceClass != null) ? resourceClass : resource.getClass();
    }

    public String getName()
    {
        return name;
    }

    public Class getResourceClass()
    {
        return resourceClass;
    }

    public Object getResource()
    {
        return resource;
    }
}
