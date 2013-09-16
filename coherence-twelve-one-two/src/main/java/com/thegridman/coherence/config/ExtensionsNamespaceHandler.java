package com.thegridman.coherence.config;

import com.tangosol.coherence.config.xml.CacheConfigNamespaceHandler;
import com.tangosol.coherence.config.xml.processor.CustomizableBuilderProcessor;
import com.tangosol.config.xml.AbstractNamespaceHandler;
import com.tangosol.config.xml.ProcessingContext;
import com.tangosol.run.xml.XmlElement;

import java.net.URI;

/**
 * @author Jonathan Knight
 */
public class ExtensionsNamespaceHandler extends AbstractNamespaceHandler
{
    public ExtensionsNamespaceHandler()
    {
        registerProcessor("service-listener", new CustomizableBuilderProcessor<>(ServiceListenerBuilder.class));
        registerProcessor("service-listeners", new ListOfInstancesProcessor<>(ServiceListenersScheme.class, ServiceListenerBuilder.class));
        registerProcessor("user-context-resource", new NamedResourceProcessor());
        registerProcessor("user-context-resources", new ListOfInstancesProcessor<>(NamedResourcesScheme.class, NamedResourceBuilder.class));
        registerProcessor(ServiceReferenceProcessor.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onStartNamespace(ProcessingContext context, XmlElement element, String prefix, URI uri)
    {
        URI coherenceURI = URI.create("class://" + CacheConfigNamespaceHandler.class.getCanonicalName());
        AbstractNamespaceHandler coherenceNamespaceHandler
                = (AbstractNamespaceHandler) context.getNamespaceHandler(coherenceURI);

        coherenceNamespaceHandler.registerProcessor(
                "distributed-scheme",
                new ExtendedServiceBuilderProcessor(ExtendedDistributedScheme.class));
        coherenceNamespaceHandler.registerProcessor(
                "invocation-scheme",
                new ExtendedServiceBuilderProcessor(ExtendedInvocationScheme.class));
        coherenceNamespaceHandler.registerProcessor(
                "optimistic-scheme",
                new ExtendedServiceBuilderProcessor(ExtendedOptimisticScheme.class));
        coherenceNamespaceHandler.registerProcessor(
                "proxy-scheme",
                new ExtendedServiceBuilderProcessor(ExtendedProxyScheme.class));
        coherenceNamespaceHandler.registerProcessor(
                "remote-cache-scheme",
                new ExtendedServiceBuilderProcessor(ExtendedRemoteCacheScheme.class));
        coherenceNamespaceHandler.registerProcessor(
                "remote-invocation-scheme",
                new ExtendedServiceBuilderProcessor(ExtendedRemoteInvocationScheme.class));
        coherenceNamespaceHandler.registerProcessor(
                "replicated-scheme",
                new ExtendedServiceBuilderProcessor(ExtendedReplicatedScheme.class));
        coherenceNamespaceHandler.registerProcessor(
                "transactional-scheme",
                new ExtendedServiceBuilderProcessor(ExtendedTransactionalScheme.class));
    }

}
