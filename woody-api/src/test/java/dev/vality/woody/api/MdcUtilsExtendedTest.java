package dev.vality.woody.api;

import dev.vality.woody.api.event.CallType;
import dev.vality.woody.api.event.ClientEventType;
import dev.vality.woody.api.event.ServiceEventType;
import dev.vality.woody.api.proxy.InstanceMethodCaller;
import dev.vality.woody.api.trace.*;
import dev.vality.woody.api.trace.context.TraceContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.MDC;

import java.lang.reflect.Method;
import java.time.Instant;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class MdcUtilsExtendedTest {

    private TraceData originalTraceData;

    @Before
    public void setUp() {
        originalTraceData = TraceContext.getCurrentTraceData();
    }

    @After
    public void tearDown() {
        MDCUtils.setExtendedFieldsEnabled(true);
        MDCUtils.removeTraceData();
        TraceContext.setCurrentTraceData(originalTraceData);
    }

    @Test
    public void testExtendedFieldsPopulated() throws Exception {
        TraceData traceData = buildTraceData();
        TraceContext.setCurrentTraceData(traceData);

        MDCUtils.putTraceData(traceData, traceData.getClientSpan());

        assertEquals("SampleService", MDC.get("rpc.server.service"));
        assertEquals("ServerCall", MDC.get("rpc.server.function"));
        assertEquals("call", MDC.get("rpc.server.type"));
        assertEquals("call handler", MDC.get("rpc.server.event"));
        assertEquals("http://service", MDC.get("rpc.server.url"));
        assertEquals("2024-01-01T00:00:00Z", MDC.get("rpc.server.deadline"));
        assertEquals("50", MDC.get("rpc.server.execution_duration_ms"));
        assertEquals("SampleService", MDC.get("rpc.client.service"));
        assertEquals("ClientCall", MDC.get("rpc.client.function"));
        assertEquals("call", MDC.get("rpc.client.type"));
        assertEquals("call service", MDC.get("rpc.client.event"));
        assertEquals("http://client", MDC.get("rpc.client.url"));
        assertEquals("2024-01-01T00:05:00Z", MDC.get("rpc.client.deadline"));
        assertEquals("25", MDC.get("rpc.client.execution_duration_ms"));
        assertEquals("realm-value", MDC.get("rpc.server.metadata.user-identity.realm"));
        assertEquals("user-123", MDC.get("rpc.client.metadata.user-identity.id"));
        assertEquals("srv-request-1", MDC.get("rpc.server.metadata.user-identity.x-request-id"));
        assertEquals("srv-deadline-iso", MDC.get("rpc.server.metadata.user-identity.x-request-deadline"));
        assertEquals("client-request-1", MDC.get("rpc.client.metadata.user-identity.x-request-id"));
        assertEquals("client-deadline-iso", MDC.get("rpc.client.metadata.user-identity.x-request-deadline"));

        traceData.getOtelSpan().end();
    }

    @Test
    public void testDisableExtendedFields() throws Exception {
        TraceData traceData = buildTraceData();
        TraceContext.setCurrentTraceData(traceData);

        MDCUtils.setExtendedFieldsEnabled(false);
        MDCUtils.putTraceData(traceData, traceData.getClientSpan());

        assertNull(MDC.get("rpc.client.service"));
        assertNull(MDC.get("rpc.client.metadata.user-identity.x-request-id"));

        traceData.getOtelSpan().end();
    }

    private TraceData buildTraceData() throws Exception {
        TraceData traceData = new TraceData();

        ContextSpan serviceSpan = traceData.getServiceSpan();
        serviceSpan.getSpan().setTraceId("serviceTrace");
        serviceSpan.getSpan().setParentId("parentService");
        serviceSpan.getSpan().setId("serviceSpanId");
        serviceSpan.getSpan().setDuration(50L);
        ContextUtils.setDeadline(serviceSpan, Instant.parse("2024-01-01T00:00:00Z"));
        serviceSpan.getMetadata().putValue(MetadataProperties.CALL_NAME, "ServerCall");
        serviceSpan.getMetadata().putValue(MetadataProperties.CALL_TYPE, CallType.CALL);
        serviceSpan.getMetadata().putValue(MetadataProperties.EVENT_TYPE, ServiceEventType.CALL_HANDLER);
        serviceSpan.getMetadata().putValue(MetadataProperties.CALL_ENDPOINT, new Endpoint<String>() {
            @Override
            public String getStringValue() {
                return "http://service";
            }

            @Override
            public String getValue() {
                return "http://service";
            }
        });
        serviceSpan.getMetadata().putValue(MetadataProperties.INSTANCE_METHOD_CALLER, createCaller("serverMethod"));
        serviceSpan.getCustomMetadata().putValue("user-identity.realm", "realm-value");
        serviceSpan.getCustomMetadata().putValue("user-identity.x-request-id", "srv-request-1");
        serviceSpan.getCustomMetadata().putValue("user-identity.x-request-deadline", "srv-deadline-iso");

        ContextSpan clientSpan = traceData.getClientSpan();
        clientSpan.getSpan().setTraceId("clientTrace");
        clientSpan.getSpan().setParentId("serviceSpanId");
        clientSpan.getSpan().setId("clientSpanId");
        clientSpan.getSpan().setDuration(25L);
        ContextUtils.setDeadline(clientSpan, Instant.parse("2024-01-01T00:05:00Z"));
        clientSpan.getMetadata().putValue(MetadataProperties.CALL_NAME, "ClientCall");
        clientSpan.getMetadata().putValue(MetadataProperties.CALL_TYPE, CallType.CALL);
        clientSpan.getMetadata().putValue(MetadataProperties.EVENT_TYPE, ClientEventType.CALL_SERVICE);
        clientSpan.getMetadata().putValue(MetadataProperties.CALL_ENDPOINT, new Endpoint<String>() {
            @Override
            public String getStringValue() {
                return "http://client";
            }

            @Override
            public String getValue() {
                return "http://client";
            }
        });
        clientSpan.getMetadata().putValue(MetadataProperties.INSTANCE_METHOD_CALLER, createCaller("clientMethod"));
        clientSpan.getCustomMetadata().putValue("user-identity.id", "user-123");
        clientSpan.getCustomMetadata().putValue("user-identity.x-request-id", "client-request-1");
        clientSpan.getCustomMetadata().putValue("user-identity.x-request-deadline", "client-deadline-iso");

        return traceData;
    }

    private InstanceMethodCaller createCaller(String methodName) throws Exception {
        Method method = SampleService.class.getDeclaredMethod(methodName);
        return new InstanceMethodCaller(method) {
            @Override
            public Object call(Object source, Object[] args) {
                return null;
            }
        };
    }

    private static class SampleService {
        public void serverMethod() {
        }

        public void clientMethod() {
        }
    }
}
