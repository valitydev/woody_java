package dev.vality.woody.api.flow.concurrent;

import dev.vality.woody.api.MDCUtils;
import dev.vality.woody.api.trace.Span;
import dev.vality.woody.api.trace.TraceData;
import dev.vality.woody.api.trace.context.TraceContext;
import io.opentelemetry.api.trace.SpanContext;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;

@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
public class TestMDCInConcurent {

    Logger log = LoggerFactory.getLogger(this.getClass());
    Runnable runnable = () -> {
        try {
            Span span = TraceContext.getCurrentTraceData().getActiveSpan().getSpan();
            io.opentelemetry.api.trace.Span otelSpan = TraceContext.getCurrentTraceData().getOtelSpan();
            SpanContext spanContext = otelSpan.getSpanContext();
            log.info("Runnable {} {} {} {} {}", span.getId(), span.getParentId(), span.getTraceId(),
                    spanContext.getSpanId(), spanContext.getTraceId());
            assertEquals(MDC.get(MDCUtils.SPAN_ID), span.getId());
            assertEquals(MDC.get(MDCUtils.TRACE_ID), span.getTraceId());
            assertEquals(MDC.get(MDCUtils.PARENT_ID), span.getParentId());
            assertEquals(MDC.get(MDCUtils.OTEL_SPAN_ID), spanContext.getSpanId());
            assertEquals(MDC.get(MDCUtils.OTEL_TRACE_ID), spanContext.getTraceId());
        } catch (Throwable t) {
            log.error("Error: ", t);
        }
    };
    Callable callable = () -> {
        try {
            Span span = TraceContext.getCurrentTraceData().getActiveSpan().getSpan();
            io.opentelemetry.api.trace.Span otelSpan = TraceContext.getCurrentTraceData().getOtelSpan();
            SpanContext spanContext = otelSpan.getSpanContext();
            log.info("Callable {} {} {} {} {}", span.getId(), span.getParentId(), span.getTraceId(),
                    spanContext.getSpanId(), spanContext.getTraceId());
            assertEquals(MDC.get(MDCUtils.SPAN_ID), span.getId());
            assertEquals(MDC.get(MDCUtils.TRACE_ID), span.getTraceId());
            assertEquals(MDC.get(MDCUtils.PARENT_ID), span.getParentId());
            assertEquals(MDC.get(MDCUtils.OTEL_TRACE_ID), spanContext.getTraceId());
            assertEquals(MDC.get(MDCUtils.OTEL_SPAN_ID), spanContext.getSpanId());
        } catch (Throwable t) {
            log.error("Error: ", t);
        }
        return null;
    };
    private WExecutorService executorService;

    @Before
    public void before() {
        executorService = new WExecutorService(Executors.newFixedThreadPool(2));

    }

    @SuppressWarnings("checkstyle:VariableDeclarationUsageDistance")
    @Test
    public void testMDCCallable() throws ExecutionException, InterruptedException {
        TraceData traceData = TraceContext.getCurrentTraceData();
        traceData.getActiveSpan().getSpan().setId("span1");
        traceData.getActiveSpan().getSpan().setTraceId("trace1");
        traceData.getActiveSpan().getSpan().setParentId("parent1");

        Future future1 = executorService.submit(callable);

        traceData.getActiveSpan().getSpan().setId("span2");
        traceData.getActiveSpan().getSpan().setTraceId("trace2");
        traceData.getActiveSpan().getSpan().setParentId("parent2");

        Future future2 = executorService.submit(callable);

        future1.get();
        future2.get();

        Assert.assertSame(traceData, TraceContext.getCurrentTraceData());
    }

    @SuppressWarnings("checkstyle:VariableDeclarationUsageDistance")
    @Test
    public void testMDCRunnable() throws ExecutionException, InterruptedException {
        TraceData traceData = TraceContext.getCurrentTraceData();
        traceData.getActiveSpan().getSpan().setId("span1");
        traceData.getActiveSpan().getSpan().setTraceId("trace1");
        traceData.getActiveSpan().getSpan().setParentId("parent1");

        Future future1 = executorService.submit(runnable);

        traceData.getActiveSpan().getSpan().setId("span2");
        traceData.getActiveSpan().getSpan().setTraceId("trace2");
        traceData.getActiveSpan().getSpan().setParentId("parent2");

        Future future2 = executorService.submit(runnable);

        future1.get();
        future2.get();

        Assert.assertSame(traceData, TraceContext.getCurrentTraceData());
    }

}
