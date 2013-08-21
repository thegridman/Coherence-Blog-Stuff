package com.thegridman.coherence.regex;

import com.tangosol.coherence.config.ParameterList;
import com.tangosol.coherence.config.builder.NamedEventInterceptorBuilder;
import com.tangosol.coherence.config.builder.ParameterizedBuilder;
import com.tangosol.coherence.config.builder.ParameterizedBuilderHelper;
import com.tangosol.config.expression.Parameter;
import com.tangosol.config.expression.ParameterResolver;
import com.tangosol.net.events.EventInterceptor;

/**
 * @author Jonathan Knight
 */
public class RegExEventInterceptorBuilder extends NamedEventInterceptorBuilder
{

    public RegExEventInterceptorBuilder(NamedEventInterceptorBuilder builder)
    {
        this.setName(builder.getName());
        this.setRegistrationBehavior(builder.getRegistrationBehavior());
        this.setOrder(builder.getOrder());
        this.setCustomBuilder(builder.getCustomBuilder());
    }

    @SuppressWarnings("unchecked")
    public RegExEventInterceptor realize(ParameterResolver resolver, ClassLoader loader, ParameterList parameters)
    {
        loader = (loader == null) ? getClass().getClassLoader() : loader;

        ParameterizedBuilder builder = getCustomBuilder();

        if (!ParameterizedBuilderHelper.realizes(builder, EventInterceptor.class, resolver, loader))
        {
            throw new IllegalArgumentException("Unable to build an EventInterceptor based on the specified class: "
                                               + String.valueOf(builder));
        }

        EventInterceptor interceptor = (EventInterceptor) builder.realize(resolver, loader, parameters);

        Parameter serviceNameParam = resolver.resolve("service-name");
        String serviceName = serviceNameParam == null ? null : serviceNameParam.evaluate(resolver).as(String.class);
        Parameter cacheNameParam = resolver.resolve("cache-name");
        String cacheName = cacheNameParam == null ? null : cacheNameParam.evaluate(resolver).as(String.class);

        return new RegExEventInterceptor(getName(), interceptor, cacheName,
                                         serviceName, getOrder(), getRegistrationBehavior());
    }

}
