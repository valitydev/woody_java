package dev.vality.woody.thrift.impl.http;

import dev.vality.woody.api.generator.TimestampIdGenerator;
import dev.vality.woody.api.trace.ContextSpan;
import dev.vality.woody.api.trace.ContextUtils;
import dev.vality.woody.api.trace.TraceData;
import dev.vality.woody.api.trace.context.TraceContext;
import dev.vality.woody.rpc.Owner;
import dev.vality.woody.rpc.OwnerServiceSrv;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.SpanContext;
import io.opentelemetry.api.trace.SpanId;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.common.CompletableResultCode;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.data.SpanData;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;
import io.opentelemetry.sdk.trace.export.SpanExporter;
import jakarta.servlet.Servlet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.EntityDetails;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.thrift.TException;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.MDC;

import java.io.IOException;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class TraceLifecycleIntegrationTest extends AbstractTest {

    private static final String REQUEST_ID_KEY = "user-identity.x-request-id";
    private static final String DEADLINE_KEY = "user-identity.x-request-deadline";
    private static final String TRACE_PARENT_HEADER = "traceparent";
    private static final String TRACE_STATE_HEADER = "tracestate";
    private static final RecordingSpanExporter SPAN_EXPORTER = new RecordingSpanExporter();
    private static SdkTracerProvider sdkTracerProvider;

    private final AtomicReference<ScenarioSettings> scenario = new AtomicReference<>();
    private final AtomicReference<InvocationSnapshot> upstreamInitial = new AtomicReference<>();
    private final AtomicReference<InvocationSnapshot> upstreamAfterCall = new AtomicReference<>();
    private final AtomicReference<InvocationSnapshot> downstreamSnapshot = new AtomicReference<>();
    private final AtomicReference<String> responseTraceParent = new AtomicReference<>();
    private final AtomicReference<String> responseTraceState = new AtomicReference<>();

    private OwnerServiceSrv.Iface downstreamClient;

    @BeforeClass
    public static void configureOpenTelemetry() {
        GlobalOpenTelemetry.resetForTest();
        SPAN_EXPORTER.reset();
        sdkTracerProvider = SdkTracerProvider.builder()
                .addSpanProcessor(SimpleSpanProcessor.create(SPAN_EXPORTER))
                .build();
        OpenTelemetrySdk.builder()
                .setTracerProvider(sdkTracerProvider)
                .setPropagators(ContextPropagators.create(W3CTraceContextPropagator.getInstance()))
                .buildAndRegisterGlobal();
    }

    @Before
    public void setUpServices() throws Exception {
        scenario.set(ScenarioSettings.fresh("noop"));
        SPAN_EXPORTER.reset();
        upstreamInitial.set(null);
        upstreamAfterCall.set(null);
        downstreamSnapshot.set(null);
        responseTraceParent.set(null);
        responseTraceState.set(null);

        OwnerServiceSrv.Iface downstreamHandler = new OwnerServiceStub() {
            @Override
            public Owner getOwner(int id) throws TException {
                InvocationSnapshot snapshot = InvocationSnapshot.capture();
                downstreamSnapshot.set(snapshot);
                ScenarioSettings settings = scenario.get();
                assertEquals(settings.expectedRequestId(), snapshot.serviceMetadataRequestId);
                assertEquals(settings.expectedDeadline(), snapshot.serviceMetadataDeadline);
                if (settings.expectTraceHeaders()) {
                    assertNotNull(snapshot.inboundTraceParent);
                    assertTrue(snapshot.inboundTraceParent.contains(settings.inboundTraceId));
                    assertEquals(settings.traceState, snapshot.inboundTraceState);
                }
                if (settings.downstreamThrows) {
                    throw settings.downstreamException;
                }
                return new Owner(id, "downstream" + id);
            }
        };

        Servlet downstreamServlet = createThriftRPCService(OwnerServiceSrv.Iface.class, downstreamHandler);

        OwnerServiceSrv.Iface upstreamHandler = new OwnerServiceStub() {
            @Override
            public Owner getOwner(int id) throws TException {
                ScenarioSettings settings = scenario.get();
                upstreamInitial.set(InvocationSnapshot.capture());
                if (settings.expectTraceHeaders()) {
                    InvocationSnapshot initial = upstreamInitial.get();
                    assertNotNull(initial);
                    assertEquals(settings.inboundTraceId, initial.serviceTraceId);
                    assertEquals(TraceContext.NO_PARENT_ID, initial.serviceParentId);
                    assertEquals(settings.traceState, initial.inboundTraceState);
                    assertEquals(settings.inboundTraceParent(), initial.inboundTraceParent);
                    assertEquals(settings.inboundRequestId, initial.serviceMetadataRequestId);
                    assertEquals(settings.inboundDeadline, initial.serviceMetadataDeadline);
                    assertEquals(settings.inboundRequestId,
                            initial.mdcServerMetadataId);
                    assertEquals(settings.inboundDeadline,
                            initial.mdcServerMetadataDeadline);
                }

                if (settings.propagateLocalMetadata) {
                    ContextUtils.setCustomMetadataValue(REQUEST_ID_KEY, settings.localRequestId);
                    ContextUtils.setCustomMetadataValue(DEADLINE_KEY, settings.localDeadline);
                    Instant deadline = settings.localDeadline != null ? Instant.parse(settings.localDeadline) : null;
                    ContextUtils.setDeadline(deadline);
                    ContextUtils.setDeadline(TraceContext.getCurrentTraceData().getServiceSpan(), deadline);
                }

                try {
                    Owner owner = downstreamClient.getOwner(id);
                    upstreamAfterCall.set(InvocationSnapshot.capture());
                    return owner;
                } catch (RuntimeException ex) {
                    upstreamAfterCall.set(InvocationSnapshot.capture());
                    throw ex;
                }
            }
        };

        Servlet upstreamServlet = createThriftRPCService(OwnerServiceSrv.Iface.class, upstreamHandler);

        ServletContextHandler context = new ServletContextHandler();
        context.setContextPath("/");
        context.addServlet(new ServletHolder("downstream", downstreamServlet), "/downstream");
        context.addServlet(new ServletHolder("upstream", upstreamServlet), "/upstream");
        HandlerCollection collection = (HandlerCollection) server.getHandler();
        collection.addHandler(context);
        context.start();

        downstreamClient = createThriftRPCClient(OwnerServiceSrv.Iface.class, new TimestampIdGenerator(), null,
                getUrlString("/downstream"));
    }

    @Test
    public void shouldStartFreshTraceAndPropagateMetadata() throws Exception {
        ScenarioSettings settings = ScenarioSettings.fresh("fresh-trace");
        scenario.set(settings);

        try (CloseableHttpClient httpClient = HttpClients.custom()
                .addResponseInterceptorLast(this::captureResponseHeaders)
                .build()) {
            OwnerServiceSrv.Iface client = createThriftRPCClient(OwnerServiceSrv.Iface.class,
                    new TimestampIdGenerator(), null, null, getUrlString("/upstream"), networkTimeout, httpClient);

            Owner owner = client.getOwner(42);
            assertEquals(new Owner(42, "downstream42"), owner);
        }

        InvocationSnapshot upstream = upstreamInitial.get();
        InvocationSnapshot after = upstreamAfterCall.get();
        InvocationSnapshot downstream = downstreamSnapshot.get();

        assertNotNull(upstream);
        assertNotNull(after);
        assertNotNull(downstream);

        assertEquals(TraceContext.NO_PARENT_ID, upstream.serviceParentId);
        assertEquals(upstream.serviceTraceId, downstream.serviceTraceId);
        assertEquals(settings.localRequestId, downstream.serviceMetadataRequestId);
        assertEquals(settings.localDeadline, downstream.serviceMetadataDeadline);
        assertEquals(settings.localRequestId, downstream.mdcServerMetadataId);
        assertEquals(settings.localDeadline, downstream.mdcServerMetadataDeadline);
        assertEquals(settings.localRequestId, after.mdcServerMetadataId);
        assertEquals(settings.localDeadline, after.mdcServerMetadataDeadline);
        assertFalse(upstream.clientSpanFilled);

        assertNotNull(upstream.otelTraceId);
        assertEquals(32, upstream.otelTraceId.length());
        assertNotEquals("00000000000000000000000000000000", upstream.otelTraceId);
        assertEquals(upstream.otelTraceId, downstream.otelTraceId);
        assertEquals(upstream.otelTraceId, after.otelTraceId);
        assertFalse(after.clientSpanFilled);
        assertNull(responseTraceState.get());
        assertNotNull(responseTraceParent.get());
        assertTrue(responseTraceParent.get().contains(upstream.otelTraceId));

        SpanStructure spans = SpanStructure.from(finishedSpans());
        spans.assertServerTraceConsistency();
        spans.assertHasRootServer();
        spans.assertHasServerHierarchy();
        spans.assertServerStatus(StatusCode.UNSET, StatusCode.OK);
        spans.assertClientStatus(StatusCode.UNSET, StatusCode.OK);
    }

    @Test
    public void shouldRestoreIncomingTraceHeadersAndEchoResponse() throws Exception {
        ScenarioSettings settings = ScenarioSettings.restored(
                "c9a6462b3f4e40c4baf3972f9b9b9d10",
                "3d2a1f0e5c7b4821",
                "vendor=ot",
                "req-restored",
                "2026-06-01T10:15:30Z");
        scenario.set(settings);

        try (CloseableHttpClient httpClient = HttpClients.custom()
                .addRequestInterceptorLast((request, entity, ctx) -> injectInboundHeaders(request, settings))
                .addResponseInterceptorLast(this::captureResponseHeaders)
                .build()) {
            OwnerServiceSrv.Iface client = createThriftRPCClient(OwnerServiceSrv.Iface.class,
                    new TimestampIdGenerator(), null, null, getUrlString("/upstream"), networkTimeout, httpClient);

            Owner owner = client.getOwner(7);
            assertEquals(new Owner(7, "downstream7"), owner);
        }

        InvocationSnapshot upstream = upstreamInitial.get();

        assertNotNull(upstream);
        assertEquals(settings.inboundTraceId, upstream.serviceTraceId);
        assertEquals(TraceContext.NO_PARENT_ID, upstream.serviceParentId);
        assertEquals(settings.inboundRequestId, upstream.serviceMetadataRequestId);
        assertEquals(settings.inboundDeadline, upstream.serviceMetadataDeadline);
        assertEquals(settings.inboundRequestId, upstream.mdcServerMetadataId);
        assertEquals(settings.inboundDeadline, upstream.mdcServerMetadataDeadline);

        InvocationSnapshot downstream = downstreamSnapshot.get();
        assertNotNull(downstream);
        assertEquals(settings.inboundTraceId, downstream.serviceTraceId);
        assertEquals(settings.expectedRequestId(), downstream.serviceMetadataRequestId);
        assertEquals(settings.expectedDeadline(), downstream.serviceMetadataDeadline);
        assertEquals(settings.traceState, downstream.inboundTraceState);
        assertEquals(settings.expectedRequestId(), downstream.mdcServerMetadataId);
        assertEquals(settings.expectedDeadline(), downstream.mdcServerMetadataDeadline);
        InvocationSnapshot after = upstreamAfterCall.get();
        assertEquals(settings.expectedRequestId(), after.mdcServerMetadataId);
        assertEquals(settings.expectedDeadline(), after.mdcServerMetadataDeadline);

        assertNotNull(responseTraceParent.get());
        assertEquals(settings.inboundTraceParent(), responseTraceParent.get());
        assertEquals(settings.traceState, responseTraceState.get());

        SpanStructure spans = SpanStructure.from(finishedSpans());
        spans.assertServerTraceConsistency();
        spans.assertHasRootServer();
        spans.assertHasServerHierarchy();
        spans.assertServerStatus(StatusCode.UNSET, StatusCode.OK);
        spans.assertClientStatus(StatusCode.UNSET, StatusCode.OK);
        assertFalse(after.clientSpanFilled);
    }

    @Test
    public void shouldMarkErrorSpanWhenDownstreamThrows() throws Exception {
        ScenarioSettings settings = ScenarioSettings.error("downstream failure");
        scenario.set(settings);

        try (CloseableHttpClient httpClient = HttpClients.custom()
                .addResponseInterceptorLast(this::captureResponseHeaders)
                .build()) {
            OwnerServiceSrv.Iface client = createThriftRPCClient(OwnerServiceSrv.Iface.class,
                    new TimestampIdGenerator(), null, null, getUrlString("/upstream"), networkTimeout, httpClient);

            try {
                client.getOwner(5);
                fail("Expected WRuntimeException");
            } catch (dev.vality.woody.api.flow.error.WRuntimeException ex) {
                assertEquals(dev.vality.woody.api.flow.error.WErrorType.UNEXPECTED_ERROR,
                        ex.getErrorDefinition().getErrorType());
                assertEquals(dev.vality.woody.api.flow.error.WErrorSource.EXTERNAL,
                        ex.getErrorDefinition().getGenerationSource());
                assertEquals(dev.vality.woody.api.flow.error.WErrorSource.EXTERNAL,
                        ex.getErrorDefinition().getErrorSource());
                assertEquals("RuntimeException:downstream failure",
                        ex.getErrorDefinition().getErrorReason());
            }
        }

        InvocationSnapshot upstream = upstreamInitial.get();
        InvocationSnapshot downstream = downstreamSnapshot.get();
        InvocationSnapshot after = upstreamAfterCall.get();

        assertNotNull(upstream);
        assertNotNull(downstream);
        assertNotNull(after);

        SpanStructure spans = SpanStructure.from(finishedSpans());
        spans.assertHasRootServer();
        spans.assertHasServerHierarchy();
    }

    @Test
    public void shouldHandleMissingMetadataGracefully() throws Exception {
        ScenarioSettings settings = ScenarioSettings.missingMetadata();
        scenario.set(settings);

        try (CloseableHttpClient httpClient = HttpClients.custom()
                .addRequestInterceptorLast((request, entity, ctx) -> {
                    request.setHeader(TRACE_PARENT_HEADER,
                            String.format("00-%s-%s-01", settings.inboundTraceId, settings.inboundSpanId));
                    request.setHeader("woody.trace-id", settings.inboundTraceId);
                    request.setHeader("woody.span-id", settings.inboundSpanId);
                    request.setHeader("woody.parent-id", TraceContext.NO_PARENT_ID);
                })
                .addResponseInterceptorLast(this::captureResponseHeaders)
                .build()) {
            OwnerServiceSrv.Iface client = createThriftRPCClient(OwnerServiceSrv.Iface.class,
                    new TimestampIdGenerator(), null, null, getUrlString("/upstream"), networkTimeout, httpClient);

            Owner owner = client.getOwner(9);
            assertEquals(new Owner(9, "downstream9"), owner);
        }

        InvocationSnapshot upstream = upstreamInitial.get();
        InvocationSnapshot downstream = downstreamSnapshot.get();

        assertNotNull(upstream);
        assertNotNull(downstream);
        assertNull(upstream.serviceMetadataRequestId);
        assertNull(downstream.serviceMetadataRequestId);
        assertNull(downstream.mdcServerMetadataId);

        SpanStructure spans = SpanStructure.from(finishedSpans());
        spans.assertServerStatus(StatusCode.UNSET, StatusCode.OK);
        spans.assertClientStatus(StatusCode.UNSET, StatusCode.OK);
    }

    private void injectInboundHeaders(HttpRequest request, ScenarioSettings settings) {
        request.setHeader(TRACE_PARENT_HEADER, settings.inboundTraceParent());
        request.setHeader(TRACE_STATE_HEADER, settings.traceState);
        request.setHeader("woody.trace-id", settings.inboundTraceId);
        request.setHeader("woody.span-id", settings.inboundSpanId);
        request.setHeader("woody.parent-id", TraceContext.NO_PARENT_ID);
        request.setHeader("woody.meta." + REQUEST_ID_KEY, settings.inboundRequestId);
        request.setHeader("woody.meta." + DEADLINE_KEY, settings.inboundDeadline);
    }

    private void captureResponseHeaders(HttpResponse response, EntityDetails entityDetails, HttpContext context)
            throws HttpException, IOException {
        if (entityDetails != null) {
            entityDetails.getContentLength();
        }
        if (context != null) {
            context.hashCode();
        }
        if (response.getFirstHeader(TRACE_PARENT_HEADER) != null) {
            responseTraceParent.set(response.getFirstHeader(TRACE_PARENT_HEADER).getValue());
        }
        if (response.getFirstHeader(TRACE_STATE_HEADER) != null) {
            responseTraceState.set(response.getFirstHeader(TRACE_STATE_HEADER).getValue());
        }
    }

    private static List<SpanData> finishedSpans() {
        if (sdkTracerProvider != null) {
            sdkTracerProvider.forceFlush().join(5, TimeUnit.SECONDS);
        }
        return SPAN_EXPORTER.getFinishedSpans();
    }

    private static final class ScenarioSettings {
        private final boolean propagateLocalMetadata;
        private final boolean downstreamThrows;
        private final RuntimeException downstreamException;
        private final String localRequestId;
        private final String localDeadline;
        private final String inboundTraceId;
        private final String inboundSpanId;
        private final String traceState;
        private final String inboundRequestId;
        private final String inboundDeadline;

        private ScenarioSettings(boolean propagateLocalMetadata, boolean downstreamThrows,
                                 RuntimeException downstreamException, String localRequestId,
                                 String localDeadline, String inboundTraceId, String inboundSpanId,
                                 String traceState, String inboundRequestId, String inboundDeadline) {
            this.propagateLocalMetadata = propagateLocalMetadata;
            this.downstreamThrows = downstreamThrows;
            this.downstreamException = downstreamException;
            this.localRequestId = localRequestId;
            this.localDeadline = localDeadline;
            this.inboundTraceId = inboundTraceId;
            this.inboundSpanId = inboundSpanId;
            this.traceState = traceState;
            this.inboundRequestId = inboundRequestId;
            this.inboundDeadline = inboundDeadline;
        }

        static ScenarioSettings fresh(String prefix) {
            String futureDeadline = Instant.now().plusSeconds(600).toString();
            return new ScenarioSettings(true, false, null,
                    prefix + "-req",
                    futureDeadline,
                    null, null, null, null, null);
        }

        static ScenarioSettings restored(String traceId, String spanId, String traceState,
                                         String requestId, String deadline) {
            return new ScenarioSettings(false, false, null,
                    null, null, traceId, spanId, traceState, requestId, deadline);
        }

        static ScenarioSettings error(String message) {
            return new ScenarioSettings(false, true, new RuntimeException(message),
                    null, null, null, null, null, null, null);
        }

        static ScenarioSettings missingMetadata() {
            return new ScenarioSettings(false, false, null,
                    null, null,
                    "d4c1ecdb5e9240b1964280a8f1f34ce1",
                    "71a3f955acbd42c9",
                    null, null, null);
        }

        boolean expectTraceHeaders() {
            return inboundTraceId != null && inboundSpanId != null;
        }

        String inboundTraceParent() {
            if (!expectTraceHeaders()) {
                return null;
            }
            return String.format("00-%s-%s-01", inboundTraceId, inboundSpanId);
        }

        String expectedRequestId() {
            if (propagateLocalMetadata && localRequestId != null) {
                return localRequestId;
            }
            return inboundRequestId;
        }

        String expectedDeadline() {
            if (propagateLocalMetadata && localDeadline != null) {
                return localDeadline;
            }
            return inboundDeadline;
        }
    }

    private static final class InvocationSnapshot {
        private final boolean clientSpanFilled;
        private final String serviceTraceId;
        private final String serviceSpanId;
        private final String serviceParentId;
        private final String serviceMetadataRequestId;
        private final String serviceMetadataDeadline;
        private final String inboundTraceParent;
        private final String inboundTraceState;
        private final String otelTraceId;
        private final String otelSpanId;
        private final String mdcServerMetadataId;
        private final String mdcServerMetadataDeadline;

        private InvocationSnapshot(boolean clientSpanFilled, String serviceTraceId, String serviceSpanId,
                                   String serviceParentId, String serviceMetadataRequestId,
                                   String serviceMetadataDeadline, String inboundTraceParent,
                                   String inboundTraceState, String otelTraceId, String otelSpanId,
                                   String mdcServerMetadataId, String mdcServerMetadataDeadline) {
            this.clientSpanFilled = clientSpanFilled;
            this.serviceTraceId = serviceTraceId;
            this.serviceSpanId = serviceSpanId;
            this.serviceParentId = serviceParentId;
            this.serviceMetadataRequestId = serviceMetadataRequestId;
            this.serviceMetadataDeadline = serviceMetadataDeadline;
            this.inboundTraceParent = inboundTraceParent;
            this.inboundTraceState = inboundTraceState;
            this.otelTraceId = otelTraceId;
            this.otelSpanId = otelSpanId;
            this.mdcServerMetadataId = mdcServerMetadataId;
            this.mdcServerMetadataDeadline = mdcServerMetadataDeadline;
        }

        private static InvocationSnapshot capture() {
            TraceData traceData = TraceContext.getCurrentTraceData();
            if (traceData == null) {
                return new InvocationSnapshot(false, null, null, null, null, null,
                        null, null, null, null, null, null);
            }
            ContextSpan serviceSpan = traceData.getServiceSpan();
            SpanContext spanContext = traceData.getOtelSpan().getSpanContext();
            return new InvocationSnapshot(traceData.getClientSpan().isFilled(),
                    serviceSpan.getSpan().getTraceId(),
                    serviceSpan.getSpan().getId(),
                    serviceSpan.getSpan().getParentId(),
                    ContextUtils.getCustomMetadataValue(serviceSpan, String.class, REQUEST_ID_KEY),
                    ContextUtils.getCustomMetadataValue(serviceSpan, String.class, DEADLINE_KEY),
                    traceData.getInboundTraceParent(),
                    traceData.getInboundTraceState(),
                    spanContext.getTraceId(),
                    spanContext.getSpanId(),
                    MDC.get("rpc.server.metadata." + REQUEST_ID_KEY),
                    MDC.get("rpc.server.metadata." + DEADLINE_KEY));
        }
    }

    private static final class SpanStructure {
        private final List<SpanData> clientSpans;
        private final List<SpanData> serverSpans;

        private SpanStructure(List<SpanData> clientSpans, List<SpanData> serverSpans) {
            this.clientSpans = clientSpans;
            this.serverSpans = serverSpans;
        }

        static SpanStructure from(List<SpanData> spans) {
            List<SpanData> clients = spans.stream()
                    .filter(span -> span.getKind() == SpanKind.CLIENT)
                    .collect(Collectors.toList());
            List<SpanData> servers = spans.stream()
                    .filter(span -> span.getKind() == SpanKind.SERVER)
                    .collect(Collectors.toList());
            if (clients.isEmpty()) {
                throw new AssertionError("Missing client spans");
            }
            if (servers.isEmpty()) {
                throw new AssertionError("Missing server spans");
            }
            return new SpanStructure(clients, servers);
        }

        void assertServerTraceConsistency() {
            var serverTraceIds = serverSpans.stream()
                    .map(SpanData::getTraceId)
                    .collect(Collectors.toSet());
            assertEquals("Server spans must share trace", 1, serverTraceIds.size());
        }

        void assertHasRootServer() {
            var serverIds = serverSpans.stream().map(SpanData::getSpanId).collect(Collectors.toSet());
            boolean hasRoot = serverSpans.stream()
                    .anyMatch(span -> !serverIds.contains(span.getParentSpanId()));
            assertTrue("Expected upstream server span", hasRoot);
        }

        void assertHasServerHierarchy() {
            var serverIds = serverSpans.stream().map(SpanData::getSpanId).collect(Collectors.toSet());
            boolean hasHierarchy = serverSpans.stream()
                    .anyMatch(span -> serverIds.contains(span.getParentSpanId()));
            assertTrue("Expected downstream server span", hasHierarchy);
        }

        void assertServerStatus(StatusCode... expected) {
            var allowed = java.util.Arrays.stream(expected).collect(Collectors.toSet());
            serverSpans.forEach(span -> assertTrue("Unexpected server status " + span.getStatus(),
                    allowed.contains(span.getStatus().getStatusCode())));
        }

        void assertClientStatus(StatusCode... expected) {
            var allowed = java.util.Arrays.stream(expected).collect(Collectors.toSet());
            clientSpans.forEach(span -> assertTrue("Unexpected client status " + span.getStatus(),
                    allowed.contains(span.getStatus().getStatusCode())));
        }

    }

    private static final class RecordingSpanExporter implements SpanExporter {
        private final List<SpanData> spans = new java.util.concurrent.CopyOnWriteArrayList<>();

        @Override
        public CompletableResultCode export(Collection<SpanData> spans) {
            this.spans.addAll(spans);
            return CompletableResultCode.ofSuccess();
        }

        @Override
        public CompletableResultCode flush() {
            return CompletableResultCode.ofSuccess();
        }

        @Override
        public CompletableResultCode shutdown() {
            spans.clear();
            return CompletableResultCode.ofSuccess();
        }

        List<SpanData> getFinishedSpans() {
            return spans;
        }

        void reset() {
            spans.clear();
        }
    }
}
