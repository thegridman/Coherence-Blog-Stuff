package com.thegridman.coherence.config;

import com.tangosol.coherence.config.scheme.RemoteInvocationScheme;
import com.tangosol.config.expression.ParameterResolver;
import com.tangosol.net.Cluster;
import com.tangosol.net.Service;

import java.util.List;

/**
 * @author Jonathan Knight
 */
public class ExtendedRemoteInvocationScheme extends RemoteInvocationScheme implements ExtendedServiceScheme
{
    private List<ServiceExtension> extensions;

    public ExtendedRemoteInvocationScheme()
    {
    }

    @Override
    public void setExtensions(List<ServiceExtension> extensions)
    {
        this.extensions = extensions;
    }

    @Override
    public Service realizeService(ParameterResolver resolver, ClassLoader loader, Cluster cluster)
    {
        Service service = super.realizeService(resolver, loader, cluster);
        if (service.isRunning())
        {
            return service;
        }

        if (extensions != null)
        {
            for (ServiceExtension extension : extensions)
            {
                extension.extendService(service, resolver, loader, cluster);
            }
        }
        return service;
    }
}
