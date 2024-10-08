package dev.vality.woody.thrift.impl.http;

import dev.vality.woody.api.AbstractServiceBuilder;
import dev.vality.woody.api.ServiceBuilder;
import dev.vality.woody.api.event.CompositeServiceEventListener;
import dev.vality.woody.api.event.ServiceEventListener;
import dev.vality.woody.api.flow.error.WErrorDefinition;
import dev.vality.woody.api.flow.error.WErrorMapper;
import dev.vality.woody.api.flow.error.WErrorType;
import dev.vality.woody.api.interceptor.*;
import dev.vality.woody.api.interceptor.ext.ExtensionBundle;
import dev.vality.woody.api.trace.ContextSpan;
import dev.vality.woody.api.trace.context.TraceContext;
import dev.vality.woody.api.trace.context.metadata.MetadataExtensionKit;
import dev.vality.woody.api.transport.TransportEventInterceptor;
import dev.vality.woody.thrift.impl.http.error.THErrorMapProcessor;
import dev.vality.woody.thrift.impl.http.event.THSEventLogListener;
import dev.vality.woody.thrift.impl.http.event.THServiceEvent;
import dev.vality.woody.thrift.impl.http.interceptor.THMessageInterceptor;
import dev.vality.woody.thrift.impl.http.interceptor.THTransportInterceptor;
import dev.vality.woody.thrift.impl.http.interceptor.ext.MetadataExtensionBundle;
import io.opentelemetry.sdk.resources.Resource;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.server.TServlet;

import jakarta.servlet.Servlet;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.BiConsumer;

public class THServiceBuilder extends AbstractServiceBuilder<Servlet> {
    private final THSEventLogListener logListener = new THSEventLogListener();
    private List<MetadataExtensionKit> metadataExtensionKits;
    private boolean logEnabled = true;
    private WErrorMapper errorMapper;

    @Override
    public THServiceBuilder withEventListener(ServiceEventListener listener) {
        return (THServiceBuilder) super.withEventListener(listener);
    }

    public THServiceBuilder withMetaExtensions(List<MetadataExtensionKit> extensionKits) {
        this.metadataExtensionKits = extensionKits;
        return this;
    }

    public THServiceBuilder withLogEnabled(boolean enabled) {
        this.logEnabled = enabled;
        return this;
    }

    public THServiceBuilder withErrorMapper(WErrorMapper errorMapper) {
        this.errorMapper = errorMapper;
        return this;
    }

    @Override
    public <T> Servlet build(Class<T> iface, T serviceHandler) {
        if (logEnabled) {
            ServiceEventListener listener = getEventListener();
            listener = (listener == null || listener == DEFAULT_EVENT_LISTENER) ? logListener :
                    new CompositeServiceEventListener(logListener, listener);
            withEventListener(listener);
        }
        return super.build(iface, serviceHandler);
    }

    protected BiConsumer<WErrorDefinition, ContextSpan> getErrorDefinitionConsumer() {
        return (eDef, contextSpan) -> {
            if (eDef.getErrorType() != WErrorType.BUSINESS_ERROR) {
                contextSpan.getMetadata().removeValue(THMetadataProperties.TH_TRANSPORT_RESPONSE_SET_FLAG);
            }
        };
    }

    @Override
    protected Runnable getErrorListener() {
        return createEventRunnable(getEventListener());
    }

    @Override
    protected Runnable getOnCallStartEventListener() {
        return createEventRunnable(getEventListener());
    }

    @Override
    protected Runnable getOnCallEndEventListener() {
        return createEventRunnable(getEventListener());
    }

    @Override
    protected Runnable getOnSendEventListener() {
        return createEventRunnable(getEventListener());
    }

    @Override
    protected Runnable getOnReceiveEventListener() {
        return createEventRunnable(getEventListener());
    }

    @Override
    protected <T> Servlet createProviderService(Class<T> serviceInterface, T handler) {
        try {
            THErrorMapProcessor errorMapProcessor =
                    THErrorMapProcessor.getInstance(false, serviceInterface, errorMapper);
            TProcessor tProcessor = createThriftProcessor(serviceInterface, handler);
            return createThriftServlet(tProcessor, createInterceptor(errorMapProcessor, true), errorMapProcessor);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected TProtocolFactory createTransferProtocolFactory() {
        return new TBinaryProtocol.Factory();
    }

    /**
     * @param isTransportLevel true - if this interceptor must be invoked on the lowers level (transport),
     *                         next is thrift protocol level wit allows message read/write detection
     */
    protected CommonInterceptor createInterceptor(THErrorMapProcessor errorMapProcessor, boolean isTransportLevel) {
        List<CommonInterceptor> interceptors = new ArrayList<>();

        if (!isTransportLevel) {
            interceptors.add(new ContainerCommonInterceptor(new THMessageInterceptor(false, true),
                    new THMessageInterceptor(false, false)));
        }

        List<ExtensionBundle> extensionBundles = Arrays.asList(new MetadataExtensionBundle(
                metadataExtensionKits == null ? Collections.EMPTY_LIST : metadataExtensionKits));
        interceptors.add(new ErrorMappingInterceptor(errorMapProcessor, getErrorDefinitionConsumer()));
        interceptors.add(new ContainerCommonInterceptor(
                isTransportLevel ? new THTransportInterceptor(extensionBundles, false, true) : null,
                new THTransportInterceptor(extensionBundles, false, false)));

        if (isTransportLevel) {
            //interceptors.add(new ProviderEventInterceptor(getOnSendEventListener(), null));
            interceptors.add(new ContextInterceptor(TraceContext.forService(),
                    new TransportEventInterceptor(getOnReceiveEventListener(), getOnReceiveEventListener(),
                            getErrorListener())));
        }
        return new CompositeInterceptor(interceptors.toArray(new CommonInterceptor[interceptors.size()]));

    }

    protected Servlet createThriftServlet(TProcessor tProcessor, CommonInterceptor servletInterceptor,
                                          THErrorMapProcessor errorMapProcessor) {
        TProtocolFactory tProtocolFactory = createTransferProtocolFactory();
        TProtocolFactory wtProtocolFactory =
                BuilderUtils.wrapProtocolFactory(tProtocolFactory, createInterceptor(errorMapProcessor, false), false);

        return new TServlet(tProcessor, wtProtocolFactory, servletInterceptor);
    }

    protected <T> TProcessor createThriftProcessor(Class<T> serviceInterface, T handler) {
        try {
            Optional<? extends Class> processorClass = Arrays.stream(serviceInterface.getDeclaringClass().getClasses())
                    .filter(cl -> cl.getSimpleName().equals("Processor")).findFirst();
            if (!processorClass.isPresent()) {
                throw new IllegalArgumentException(
                        "Service interface doesn't conform to Thrift generated class structure");
            }
            if (!TProcessor.class.isAssignableFrom(processorClass.get())) {
                throw new IllegalArgumentException("Service class doesn't conform to Thrift generated class structure");
            }
            Constructor constructor = processorClass.get().getConstructor(serviceInterface);
            if (constructor == null) {
                throw new IllegalArgumentException("Service class doesn't have required constructor to be created");
            }
            return (TProcessor) constructor.newInstance(handler);
        } catch (NoSuchMethodException |
                InstantiationException |
                IllegalAccessException |
                InvocationTargetException e) {
            throw new IllegalArgumentException("Failed to createCtxBundle provider service", e);
        }
    }

    private Runnable createEventRunnable(ServiceEventListener eventListener) {
        return () -> eventListener.notifyEvent(new THServiceEvent(TraceContext.getCurrentTraceData()));
    }
}
