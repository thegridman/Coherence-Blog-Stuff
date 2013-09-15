package com.thegridman.coherence.config;

import com.tangosol.coherence.config.builder.ParameterizedBuilder;
import com.tangosol.config.expression.ParameterResolver;
import com.tangosol.net.Cluster;
import com.tangosol.net.Service;
import com.tangosol.util.ResourceRegistry;
import com.tangosol.util.SimpleResourceRegistry;

import java.util.List;

/**
 * @author Jonathan Knight
 */
public class NamedResourcesScheme
        implements ServiceExtension, ListOfInstancesProcessor.ListOfInstancesScheme<NamedResourceBuilder>
{
    private List<NamedResourceBuilder> instances;

    public NamedResourcesScheme()
    {
    }

    @Override
    public void setInstances(List<NamedResourceBuilder> instances)
    {
        this.instances = instances;
    }

    @Override
    public void extendService(Service service, ParameterResolver resolver, ClassLoader loader, Cluster cluster)
    {
        if (instances == null || instances.isEmpty())
        {
            return;
        }

        Object userContext = service.getUserContext();
        ResourceRegistry registry;
        if (userContext != null && userContext instanceof ResourceRegistry)
        {
            registry = (ResourceRegistry) userContext;
        }
        else
        {
            registry = new SimpleResourceRegistry();
        }

        for (ParameterizedBuilder<NamedResource> builder : instances)
        {
            NamedResource resource = builder.realize(resolver, loader, null);
            registry.registerResource(resource.getResourceClass(), resource.getName(), resource.getResource());
        }

        service.setUserContext(registry);
    }
}
