package dev.vality.woody.api.interceptor;

import dev.vality.woody.api.trace.Span;
import dev.vality.woody.api.trace.TraceData;
import dev.vality.woody.api.trace.context.TraceContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.MDC;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class CompositeInterceptorTest {

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
        TraceContext.setCurrentTraceData(originalTraceData);
    }

    @Test
    public void responseInterceptorsRunAfterFailureToCleanupContext() {
        fillServiceSpan(testTraceData);
        CommonInterceptor failingInterceptor = new ResponseResultInterceptor(false);
        ContextInterceptor contextInterceptor = new ContextInterceptor(TraceContext.forService(), null);
        CompositeInterceptor interceptor = new CompositeInterceptor(failingInterceptor, contextInterceptor);

        assertTrue(interceptor.interceptRequest(testTraceData, null));
        assertTrue(testTraceData.getServiceSpan().isStarted());

        assertFalse(interceptor.interceptResponse(testTraceData, null));
        assertFalse(TraceContext.getCurrentTraceData().getServiceSpan().isFilled());
    }

    @Test
    public void responseInterceptorsRunAfterExceptionBeforeRethrow() {
        RuntimeException expected = new RuntimeException("response error");
        ThrowingResponseInterceptor throwingInterceptor = new ThrowingResponseInterceptor(expected);
        RecordingInterceptor recordingInterceptor = new RecordingInterceptor();
        CompositeInterceptor interceptor = new CompositeInterceptor(throwingInterceptor, recordingInterceptor);

        try {
            interceptor.interceptResponse(testTraceData, null);
            fail("Expected response error");
        } catch (RuntimeException e) {
            assertSame(expected, e);
        }

        assertTrue(recordingInterceptor.responseInvoked);
    }

    private static void fillServiceSpan(TraceData traceData) {
        Span span = traceData.getServiceSpan().getSpan();
        span.setTraceId("trace");
        span.setParentId("parent");
        span.setId("span");
    }

    private static class ResponseResultInterceptor extends EmptyCommonInterceptor {
        private final boolean responseResult;

        private ResponseResultInterceptor(boolean responseResult) {
            this.responseResult = responseResult;
        }

        @Override
        public boolean interceptResponse(TraceData traceData, Object providerContext, Object... contextParams) {
            return responseResult;
        }
    }

    private static class ThrowingResponseInterceptor extends EmptyCommonInterceptor {
        private final RuntimeException exception;

        private ThrowingResponseInterceptor(RuntimeException exception) {
            this.exception = exception;
        }

        @Override
        public boolean interceptResponse(TraceData traceData, Object providerContext, Object... contextParams) {
            throw exception;
        }
    }

    private static class RecordingInterceptor extends EmptyCommonInterceptor {
        private boolean responseInvoked;

        @Override
        public boolean interceptResponse(TraceData traceData, Object providerContext, Object... contextParams) {
            responseInvoked = true;
            return super.interceptResponse(traceData, providerContext, contextParams);
        }
    }
}
