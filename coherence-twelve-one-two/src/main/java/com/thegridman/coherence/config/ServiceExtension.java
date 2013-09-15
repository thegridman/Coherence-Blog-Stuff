package com.thegridman.coherence.config;

import com.tangosol.config.expression.ParameterResolver;
import com.tangosol.net.Cluster;
import com.tangosol.net.Service;

/**
 * @author Jonathan Knight
 */
public interface ServiceExtension
{
    void extendService(Service service, ParameterResolver resolver, ClassLoader loader, Cluster cluster);
}
