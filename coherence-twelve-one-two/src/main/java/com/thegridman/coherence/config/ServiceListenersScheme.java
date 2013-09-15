package com.thegridman.coherence.config;

import com.tangosol.config.expression.ParameterResolver;
import com.tangosol.net.Cluster;
import com.tangosol.net.Service;
import com.tangosol.util.ServiceListener;

import java.util.List;

/**
 * @author Jonathan Knight
 */
public class ServiceListenersScheme
        implements ServiceExtension, ListOfInstancesProcessor.ListOfInstancesScheme<ServiceListenerBuilder>
{
    private List<ServiceListenerBuilder> instances;

    public ServiceListenersScheme()
    {
    }

    @Override
    public void setInstances(List<ServiceListenerBuilder> instances)
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

        for (ServiceListenerBuilder builder : instances)
        {
            ServiceListener listener = builder.realize(resolver, loader, null);
            service.addServiceListener(listener);
        }
    }
}
