package com.thegridman.coherence.config;

import com.tangosol.coherence.config.ParameterList;
import com.tangosol.coherence.config.builder.BuilderCustomization;
import com.tangosol.coherence.config.builder.ParameterizedBuilder;
import com.tangosol.coherence.config.builder.ParameterizedBuilderHelper;
import com.tangosol.config.expression.ParameterResolver;
import com.tangosol.util.ServiceListener;

/**
 * @author Jonathan Knight
 */
public class ServiceListenerBuilder
        implements ParameterizedBuilder<ServiceListener>, BuilderCustomization<ServiceListener>
{
    private ParameterizedBuilder<ServiceListener> builder;

    public ServiceListenerBuilder()
    {
    }

    @Override
    public ServiceListener realize(ParameterResolver resolver, ClassLoader loader, ParameterList listParameters)
    {
        loader = loader == null ? getClass().getClassLoader() : loader;

        if (!ParameterizedBuilderHelper.realizes(builder, ServiceListener.class, resolver, loader))
        {
          throw new IllegalArgumentException(
                  "Unable to build an ServiceListener based on the specified class: "
                  + String.valueOf(builder));
        }

        return builder.realize(resolver, loader, listParameters);
    }

    @Override
    public ParameterizedBuilder<ServiceListener> getCustomBuilder()
    {
        return this.builder;
    }

    @Override
    public void setCustomBuilder(ParameterizedBuilder<ServiceListener> builder)
    {
        this.builder = builder;
    }
}
