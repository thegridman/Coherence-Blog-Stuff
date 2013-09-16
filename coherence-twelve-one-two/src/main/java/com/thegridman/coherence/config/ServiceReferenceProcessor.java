package com.thegridman.coherence.config;

import com.tangosol.config.ConfigurationException;
import com.tangosol.config.expression.Expression;
import com.tangosol.config.xml.ElementProcessor;
import com.tangosol.config.xml.ProcessingContext;
import com.tangosol.config.xml.XmlSimpleName;
import com.tangosol.run.xml.XmlElement;

import java.text.ParseException;

/**
 * @author Jonathan Knight
 */
@XmlSimpleName("service-reference")
public class ServiceReferenceProcessor implements ElementProcessor<ServiceReferenceExpression>
{
    public ServiceReferenceProcessor()
    {
    }

    @Override
    public ServiceReferenceExpression process(ProcessingContext context, XmlElement element) throws ConfigurationException
    {
        try
        {
            Expression serviceNameExpression = context.getExpressionParser().parse(element.getString(), String.class);
            return new ServiceReferenceExpression(serviceNameExpression);
        }
        catch (ParseException e)
        {
            throw new ConfigurationException("Error parsing service-reference", "", e);
        }
    }
}
