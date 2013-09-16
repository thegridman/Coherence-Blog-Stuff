package com.thegridman.coherence.config;

import com.tangosol.config.expression.Expression;
import com.tangosol.config.expression.ParameterResolver;
import com.tangosol.config.expression.Value;
import com.tangosol.net.CacheFactory;
import com.tangosol.net.Service;

/**
 * @author Jonathan Knight
 */
public class ServiceReferenceExpression implements Expression<Service>
{
    private final Expression<?> serviceNameExpression;

    public ServiceReferenceExpression(Expression<?> serviceNameExpression)
    {
        this.serviceNameExpression = serviceNameExpression;
    }

    @Override
    public Service evaluate(ParameterResolver resolver)
    {
        String serviceName = new Value(this.serviceNameExpression.evaluate(resolver)).as(String.class);
        return CacheFactory.getService(serviceName);
    }
}
