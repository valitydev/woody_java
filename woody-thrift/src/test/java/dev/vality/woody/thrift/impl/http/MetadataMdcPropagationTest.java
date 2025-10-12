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

import static org.junit.Assert.*;

public class MetadataMdcPropagationTest extends AbstractTest {

    private static final String X_REQUEST_ID = "068e67b4-74bc-4333-9c14-090e6acc3227";
    private static final String X_REQUEST_DEADLINE = "2025-01-01T12:30:00Z";
    private static final String TRACE_ID = "4e0e9f8d8d8044f9b65a3b0f5cdfc2d1";
    private static final String SPAN_ID = "1a2b3c4d5e6f7081";
    private static final String TRACE_STATE = "rojo=00f067aa0ba902b7,congo=t61rcWkgMzE";

    private final AtomicReference<String> upstreamMetadataId = new AtomicReference<>();
    private final AtomicReference<String> upstreamMetadataDeadline = new AtomicReference<>();
    private final AtomicReference<String> upstreamMdcId = new AtomicReference<>();
    private final AtomicReference<String> upstreamMdcDeadline = new AtomicReference<>();
    private final AtomicReference<String> downstreamMetadataId = new AtomicReference<>();
    private final AtomicReference<String> downstreamMetadataDeadline = new AtomicReference<>();
    private final AtomicReference<String> downstreamMdcId = new AtomicReference<>();
    private final AtomicReference<String> downstreamMdcDeadline = new AtomicReference<>();
    private final AtomicReference<String> upstreamOtelTraceId = new AtomicReference<>();
    private final AtomicReference<String> downstreamOtelTraceId = new AtomicReference<>();
    private final AtomicReference<String> upstreamTraceState = new AtomicReference<>();
    private final AtomicReference<String> downstreamTraceState = new AtomicReference<>();
    private final AtomicReference<String> upstreamTraceParent = new AtomicReference<>();
    private final AtomicReference<String> downstreamTraceParent = new AtomicReference<>();
    private final AtomicReference<String> responseTraceParent = new AtomicReference<>();
    private final AtomicReference<String> responseTraceState = new AtomicReference<>();

    private OwnerServiceSrv.Iface downstreamClient;

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
                downstreamOtelTraceId.set(
                        TraceContext.getCurrentTraceData().getOtelSpan().getSpanContext().getTraceId());
                downstreamTraceState.set(TraceContext.getCurrentTraceData().getInboundTraceState());
                downstreamTraceParent.set(TraceContext.getCurrentTraceData().getInboundTraceParent());
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
                upstreamOtelTraceId.set(
                        TraceContext.getCurrentTraceData().getOtelSpan().getSpanContext().getTraceId());
                upstreamTraceState.set(TraceContext.getCurrentTraceData().getInboundTraceState());
                upstreamTraceParent.set(TraceContext.getCurrentTraceData().getInboundTraceParent());

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
                .addResponseInterceptorLast(this::captureResponseHeaders)
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
        String upstreamTraceId = upstreamOtelTraceId.get();
        String downstreamTraceId = downstreamOtelTraceId.get();
        assertNotNull(upstreamTraceId);
        assertNotNull(downstreamTraceId);
        assertEquals(upstreamTraceId, downstreamTraceId);
        assertEquals(32, upstreamTraceId.length());
        assertNotEquals("00000000000000000000000000000000", upstreamTraceId);
        assertEquals(TRACE_STATE, upstreamTraceState.get());
        assertEquals(TRACE_STATE, downstreamTraceState.get());
        assertNotNull("Upstream traceparent must be captured", upstreamTraceParent.get());
        assertNotNull("Downstream traceparent must be captured", downstreamTraceParent.get());
        assertNotNull("traceparent in HTTP response must be present", responseTraceParent.get());
        assertTrue("Upstream traceparent should contain the original trace ID",
                upstreamTraceParent.get().contains(TRACE_ID));
        assertTrue("Downstream traceparent should contain the original trace ID",
                downstreamTraceParent.get().contains(TRACE_ID));
        assertTrue("Response traceparent should contain the original trace ID",
                responseTraceParent.get().contains(TRACE_ID));
        assertEquals(TRACE_STATE, responseTraceState.get());
    }

    private void injectHeaders(HttpRequest request, EntityDetails entity, HttpContext context)
            throws HttpException, IOException {
        if (entity != null) {
            entity.getContentLength();
        }
        if (context != null) {
            context.hashCode();
        }
        request.setHeader("woody.meta.user-identity.x-request-id", X_REQUEST_ID);
        request.setHeader("woody.meta.user-identity.x-request-deadline", X_REQUEST_DEADLINE);
        request.setHeader("woody.trace-id", TRACE_ID);
        request.setHeader("woody.span-id", SPAN_ID);
        request.setHeader("woody.parent-id", TraceContext.NO_PARENT_ID);
        request.setHeader("traceparent", String.format("00-%s-%s-01", TRACE_ID, SPAN_ID));
        request.setHeader("tracestate", TRACE_STATE);
    }

    private void captureResponseHeaders(org.apache.hc.core5.http.HttpResponse response,
                                        EntityDetails entityDetails,
                                        HttpContext context) throws HttpException, IOException {
        if (entityDetails != null) {
            entityDetails.getContentType();
        }
        if (context != null) {
            context.hashCode();
        }
        var traceParentHeader = response.getFirstHeader("traceparent");
        if (traceParentHeader != null) {
            responseTraceParent.set(traceParentHeader.getValue());
        }
        var traceStateHeader = response.getFirstHeader("tracestate");
        if (traceStateHeader != null) {
            responseTraceState.set(traceStateHeader.getValue());
        }
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
        upstreamOtelTraceId.set(null);
        downstreamOtelTraceId.set(null);
        upstreamTraceState.set(null);
        downstreamTraceState.set(null);
        upstreamTraceParent.set(null);
        downstreamTraceParent.set(null);
        responseTraceParent.set(null);
        responseTraceState.set(null);
    }
}
