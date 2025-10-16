package dev.vality.woody.api.proxy.tracer;

import dev.vality.woody.api.MDCUtils;
import dev.vality.woody.api.event.ServiceEventType;
import dev.vality.woody.api.proxy.InstanceMethodCaller;
import dev.vality.woody.api.trace.ContextSpan;
import dev.vality.woody.api.trace.MetadataProperties;
import dev.vality.woody.api.trace.TraceData;
import dev.vality.woody.api.trace.context.TraceContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.MDC;

import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TargetCallTracerMdcTest {

    private TraceData originalTraceData;
    private TraceContext traceContext;

    @Before
    public void setUp() {
        originalTraceData = TraceContext.getCurrentTraceData();
        TraceContext.setCurrentTraceData(new TraceData());
        MDC.clear();
    }

    @After
    public void tearDown() {
        try {
            if (traceContext != null) {
                traceContext.destroy(false);
            }
        } finally {
            TraceContext.setCurrentTraceData(originalTraceData);
            MDCUtils.removeTraceData();
        }
    }

    @Test
    public void shouldPopulateRpcFieldsAfterTargetCallTracer() throws Exception {
        TraceData traceData = TraceContext.getCurrentTraceData();
        ContextSpan serviceSpan = traceData.getServiceSpan();
        serviceSpan.getSpan().setTraceId("trace-1");
        serviceSpan.getSpan().setParentId("parent-1");
        serviceSpan.getSpan().setId("span-1");
        serviceSpan.getMetadata().putValue(MetadataProperties.CALL_NAME, "ServerCall");

        traceContext = TraceContext.forService();
        traceContext.init();

        assertNull("RPC service field must not be populated before TargetCallTracer",
                MDC.get(MDCUtils.TRACE_RPC_SERVER_PREFIX + "service"));

        InstanceMethodCaller caller = createCaller("sampleHandler");
        TargetCallTracer.forServer().beforeCall(new Object[0], caller);

        assertEquals("SampleService", MDC.get(MDCUtils.TRACE_RPC_SERVER_PREFIX + "service"));
        assertEquals("ServerCall", MDC.get(MDCUtils.TRACE_RPC_SERVER_PREFIX + "function"));
        assertEquals("call handler", MDC.get(MDCUtils.TRACE_RPC_SERVER_PREFIX + "event"));
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
        public void sampleHandler() {
        }
    }
}
