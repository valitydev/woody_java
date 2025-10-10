package dev.vality.woody.thrift.impl.http;

import dev.vality.woody.api.event.ServiceEventListener;
import dev.vality.woody.api.generator.TimestampIdGenerator;
import dev.vality.woody.api.trace.ContextUtils;
import dev.vality.woody.api.trace.context.TraceContext;
import dev.vality.woody.api.trace.context.metadata.MetadataExtensionKit;
import dev.vality.woody.rpc.Owner;
import dev.vality.woody.rpc.OwnerServiceSrv;
import jakarta.servlet.Servlet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.EntityDetails;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.thrift.TException;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.MDC;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class MetadataMdcPropagationTest extends AbstractTest {

    @Override
    protected <T> Servlet createThriftRPCService(Class<T> iface, T handler,
                                                 ServiceEventListener eventListener,
                                                 List<MetadataExtensionKit> extensionKits) {
        THServiceBuilder serviceBuilder = new THServiceBuilder();
        serviceBuilder.withLogEnabled(false);
        if (eventListener != null) {
            serviceBuilder.withEventListener(eventListener);
        }
        serviceBuilder.withMetaExtensions(extensionKits);
        return serviceBuilder.build(iface, handler);
    }

    private static final String X_REQUEST_ID = "068e67b4-74bc-4333-9c14-090e6acc3227";
    private static final String X_REQUEST_DEADLINE = "2025-01-01T12:30:00Z";
    private static final String TRACE_ID = "GZzfNSaAAAA";
    private static final String SPAN_ID = "1823351624202973184";

    private final AtomicReference<String> upstreamMetadataId = new AtomicReference<>();
    private final AtomicReference<String> upstreamMetadataDeadline = new AtomicReference<>();
    private final AtomicReference<String> upstreamMdcId = new AtomicReference<>();
    private final AtomicReference<String> upstreamMdcDeadline = new AtomicReference<>();
    private final AtomicReference<String> downstreamMetadataId = new AtomicReference<>();
    private final AtomicReference<String> downstreamMetadataDeadline = new AtomicReference<>();
    private final AtomicReference<String> downstreamMdcId = new AtomicReference<>();
    private final AtomicReference<String> downstreamMdcDeadline = new AtomicReference<>();

    private OwnerServiceSrv.Iface downstreamClient;

    @Before
    public void setUpServices() throws Exception {
        OwnerServiceSrv.Iface downstreamHandler = new OwnerServiceStub() {
            @Override
            public Owner getOwner(int id) throws TException {
                downstreamMetadataId.set(ContextUtils.getCustomMetadataValue(String.class,
                        "user-identity.x-request-id"));
                downstreamMetadataDeadline.set(ContextUtils.getCustomMetadataValue(String.class,
                        "user-identity.x-request-deadline"));
                downstreamMdcId.set(MDC.get("rpc.server.metadata.user-identity.x-request-id"));
                downstreamMdcDeadline.set(MDC.get("rpc.server.metadata.user-identity.x-request-deadline"));
                return new Owner(id, "downstream");
            }
        };

        Servlet downstreamServlet = createThriftRPCService(OwnerServiceSrv.Iface.class, downstreamHandler);

        OwnerServiceSrv.Iface upstreamHandler = new OwnerServiceStub() {
            @Override
            public Owner getOwner(int id) throws TException {
                upstreamMetadataId.set(ContextUtils.getCustomMetadataValue(String.class,
                        "user-identity.x-request-id"));
                upstreamMetadataDeadline.set(ContextUtils.getCustomMetadataValue(String.class,
                        "user-identity.x-request-deadline"));
                upstreamMdcId.set(MDC.get("rpc.server.metadata.user-identity.x-request-id"));
                upstreamMdcDeadline.set(MDC.get("rpc.server.metadata.user-identity.x-request-deadline"));

                Owner result = downstreamClient.getOwner(id);

                assertNotNull("Active trace context must be available", TraceContext.getCurrentTraceData());
                return result;
            }
        };

        Servlet upstreamServlet = createThriftRPCService(OwnerServiceSrv.Iface.class, upstreamHandler);

        org.eclipse.jetty.servlet.ServletContextHandler context = new org.eclipse.jetty.servlet.ServletContextHandler();
        context.setContextPath("/");
        context.addServlet(new org.eclipse.jetty.servlet.ServletHolder("downstream", downstreamServlet),
                "/downstream");
        context.addServlet(new org.eclipse.jetty.servlet.ServletHolder("upstream", upstreamServlet),
                "/upstream");
        ((org.eclipse.jetty.server.handler.HandlerCollection) server.getHandler()).addHandler(context);
        context.start();

        downstreamClient = createThriftRPCClient(OwnerServiceSrv.Iface.class, new TimestampIdGenerator(), null,
                getUrlString("/downstream"));
    }

    @Test
    public void shouldPropagateMetadataHeadersAndPopulateMdc() throws Exception {
        clearCapturedValues();

        try (CloseableHttpClient httpClient = HttpClients.custom()
                .addRequestInterceptorLast(this::injectHeaders)
                .build()) {
            OwnerServiceSrv.Iface entryClient = createThriftRPCClient(OwnerServiceSrv.Iface.class,
                    new TimestampIdGenerator(), null, null, getUrlString("/upstream"), networkTimeout, httpClient);

            entryClient.getOwner(42);
        }

        assertEquals(X_REQUEST_ID, upstreamMetadataId.get());
        assertEquals(X_REQUEST_DEADLINE, upstreamMetadataDeadline.get());
        assertEquals(X_REQUEST_ID, upstreamMdcId.get());
        assertEquals(X_REQUEST_DEADLINE, upstreamMdcDeadline.get());

        assertEquals(X_REQUEST_ID, downstreamMetadataId.get());
        assertEquals(X_REQUEST_DEADLINE, downstreamMetadataDeadline.get());
        assertEquals(X_REQUEST_ID, downstreamMdcId.get());
        assertEquals(X_REQUEST_DEADLINE, downstreamMdcDeadline.get());
    }

    private void injectHeaders(HttpRequest request, EntityDetails entity, HttpContext context)
            throws HttpException, IOException {
        request.addHeader("woody.meta.user-identity.x-request-id", X_REQUEST_ID);
        request.addHeader("woody.meta.user-identity.x-request-deadline", X_REQUEST_DEADLINE);
        request.addHeader("woody.trace-id", TRACE_ID);
        request.addHeader("woody.span-id", SPAN_ID);
        request.addHeader("woody.parent-id", TraceContext.NO_PARENT_ID);
        request.addHeader("traceparent", String.format("00-%s-%s-01", TRACE_ID.toLowerCase(),
                SPAN_ID.substring(0, 16)));
    }

    private void clearCapturedValues() {
        upstreamMetadataId.set(null);
        upstreamMetadataDeadline.set(null);
        upstreamMdcId.set(null);
        upstreamMdcDeadline.set(null);
        downstreamMetadataId.set(null);
        downstreamMetadataDeadline.set(null);
        downstreamMdcId.set(null);
        downstreamMdcDeadline.set(null);
    }
}
