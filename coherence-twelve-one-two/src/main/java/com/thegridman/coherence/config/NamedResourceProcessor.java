package com.thegridman.coherence.config;

import com.tangosol.config.ConfigurationException;
import com.tangosol.config.expression.Expression;
import com.tangosol.config.expression.LiteralExpression;
import com.tangosol.config.xml.ElementProcessor;
import com.tangosol.config.xml.ProcessingContext;
import com.tangosol.run.xml.XmlElement;

import java.util.Map;

/**
 * @author Jonathan Knight
 */
public class NamedResourceProcessor implements ElementProcessor<NamedResourceBuilder>
{

    @SuppressWarnings("unchecked")
    @Override
    public NamedResourceBuilder process(ProcessingContext context, XmlElement element) throws ConfigurationException
    {
        NamedResourceBuilder builder = new NamedResourceBuilder();
        context.inject(builder, element);

        Map<String,?> map = context.processRemainingElementsOf(element);
        if (map.size() > 0)
        {
            Object value = map.values().iterator().next();
            Expression expression = (value instanceof Expression)
                    ? (Expression) value : new LiteralExpression(value);
            builder.setResourceExpression(expression);
        }

        return builder;
    }
}
