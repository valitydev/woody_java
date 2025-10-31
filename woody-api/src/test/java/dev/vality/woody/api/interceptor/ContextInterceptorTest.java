package dev.vality.woody.api.interceptor;

import dev.vality.woody.api.MDCUtils;
import dev.vality.woody.api.trace.TraceData;
import dev.vality.woody.api.trace.context.TraceContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.MDC;

import static org.junit.Assert.*;

public class ContextInterceptorTest {

    private TraceData originalTraceData;
    private TraceData testTraceData;

    @Before
    public void setUp() {
        originalTraceData = TraceContext.getCurrentTraceData();
        testTraceData = new TraceData();
        TraceContext.setCurrentTraceData(testTraceData);
        MDC.clear();
    }

    @After
    public void tearDown() {
        MDC.clear();
        if (testTraceData != null) {
            testTraceData.getOtelSpan().end();
        }
        TraceContext.setCurrentTraceData(originalTraceData);
    }

    @Test
    public void skipInitWhenServiceSpanEmpty() {
        RecordingInterceptor delegate = new RecordingInterceptor();
        ContextInterceptor interceptor = new ContextInterceptor(TraceContext.forService(), delegate);

        TraceData current = TraceContext.getCurrentTraceData();
        MDC.put(MDCUtils.SPAN_ID, "existing");

        assertTrue(interceptor.interceptRequest(current, null));
        assertTrue(delegate.requestInvoked);

        interceptor.interceptResponse(current, null);

        assertTrue(delegate.responseInvoked);
        assertNull(MDC.get(MDCUtils.SPAN_ID));
        assertFalse(TraceContext.getCurrentTraceData().getServiceSpan().isFilled());
    }

    @Test
    public void destroyWithoutTraceDataClearsMdc() {
        TraceContext.setCurrentTraceData(null);
        MDC.put(MDCUtils.SPAN_ID, "value");

        TraceContext.forService().destroy();

        assertNull(MDC.get(MDCUtils.SPAN_ID));
        TraceContext.setCurrentTraceData(testTraceData);
    }

    private static class RecordingInterceptor extends EmptyCommonInterceptor {
        private boolean requestInvoked;
        private boolean responseInvoked;

        @Override
        public boolean interceptRequest(TraceData traceData, Object providerContext, Object... contextParams) {
            requestInvoked = true;
            return super.interceptRequest(traceData, providerContext, contextParams);
        }

        @Override
        public boolean interceptResponse(TraceData traceData, Object providerContext, Object... contextParams) {
            responseInvoked = true;
            return super.interceptResponse(traceData, providerContext, contextParams);
        }
    }
}
