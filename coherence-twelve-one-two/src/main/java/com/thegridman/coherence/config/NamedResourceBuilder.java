package com.thegridman.coherence.config;

import com.tangosol.coherence.config.ParameterList;
import com.tangosol.coherence.config.builder.ParameterizedBuilder;
import com.tangosol.coherence.config.builder.ParameterizedBuilderHelper;
import com.tangosol.config.annotation.Injectable;
import com.tangosol.config.expression.Expression;
import com.tangosol.config.expression.ParameterResolver;
import com.tangosol.config.expression.Value;
import com.tangosol.util.Base;

/**
 * @author Jonathan Knight
 */
public class NamedResourceBuilder implements ParameterizedBuilder<NamedResource>
{
    private String resourceName;

    private String resourceClassName;

    private Expression resourceExpression;

    public String getResourceName()
    {
        return resourceName;
    }

    @Injectable
    public void setResourceName(String resourceName)
    {
        this.resourceName = resourceName;
    }

    public String getResourceType()
    {
        return resourceClassName;
    }

    @Injectable
    public void setResourceType(String resourceClassName)
    {
        this.resourceClassName = resourceClassName;
    }

    public Expression getResourceExpression()
    {
        return resourceExpression;
    }

    public void setResourceExpression(Expression resourceExpression)
    {
        this.resourceExpression = resourceExpression;
    }

    @Override
    public NamedResource realize(ParameterResolver resolver, ClassLoader loader, ParameterList parameters)
    {
        try
        {
            Class<?> resourceClass = null;

            if (resourceClassName != null && !resourceClassName.isEmpty())
            {
                resourceClass = loader.loadClass(resourceClassName);
            }

            Object resource = null;
            if (resourceExpression != null)
            {
                resource = resourceExpression.evaluate(resolver);
            }

            if (resource instanceof Value)
            {
                resource = ((Value)resource).get();
            }
            else if (resource instanceof ParameterizedBuilder)
            {
                ParameterizedBuilder builder = (ParameterizedBuilder) resource;
                if (resourceClass != null
                    && !ParameterizedBuilderHelper.realizes(builder, resourceClass, resolver, loader))
                {
                  throw new IllegalArgumentException("The specified resource class [" +
                                                     resourceClass + "] is not built by: "
                                                     + String.valueOf(builder));
                }
                resource = builder.realize(resolver, loader, parameters);
            }

            return new NamedResource(resourceName, resourceClass, resource);
        }
        catch (ClassNotFoundException e)
        {
            throw Base.ensureRuntimeException(e);
        }
    }
}
