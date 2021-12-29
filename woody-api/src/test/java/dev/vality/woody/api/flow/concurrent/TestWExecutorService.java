package dev.vality.woody.api.flow.concurrent;

import dev.vality.woody.api.trace.TraceData;
import dev.vality.woody.api.trace.context.TraceContext;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

public class TestWExecutorService {
    private WExecutorService executorService;

    @Before
    public void before() {
        executorService = new WExecutorService(Executors.newSingleThreadExecutor());

    }

    @Test
    public void testRunnable() throws ExecutionException, InterruptedException {
        AtomicBoolean hasErrors = new AtomicBoolean();
        TraceData traceData = TraceContext.getCurrentTraceData();
        traceData.getActiveSpan().getSpan().setId("testID");
        traceData.getActiveSpan().getMetadata().putValue(Boolean.TRUE.toString(), new Object());
        Future future = executorService.submit(() -> {
            try {
                TraceData currentData = TraceContext.getCurrentTraceData();
                Assert.assertNotSame(traceData, currentData);
                Assert.assertEquals(traceData.getActiveSpan().getSpan().getId(),
                        currentData.getActiveSpan().getSpan().getId());
                Assert.assertSame(traceData.getActiveSpan().getMetadata().getValue(Boolean.TRUE.toString()),
                        currentData.getActiveSpan().getMetadata().getValue(Boolean.TRUE.toString()));
                Assert.assertNotSame(traceData, currentData);
            } catch (Throwable t) {
                hasErrors.set(true);
                t.printStackTrace();
            }
        });
        future.get();
        Assert.assertSame(traceData, TraceContext.getCurrentTraceData());
        Assert.assertFalse(hasErrors.get());

    }

    @Test
    public void testCallable() throws ExecutionException, InterruptedException {
        AtomicBoolean hasErrors = new AtomicBoolean();
        TraceData traceData = TraceContext.getCurrentTraceData();
        traceData.getActiveSpan().getSpan().setId("testID");
        traceData.getActiveSpan().getMetadata().putValue(Boolean.TRUE.toString(), new Object());
        Future future = executorService.submit(() -> {
            try {
                TraceData currentData = TraceContext.getCurrentTraceData();
                Assert.assertNotSame(traceData, currentData);
                Assert.assertEquals(traceData.getActiveSpan().getSpan().getId(),
                        currentData.getActiveSpan().getSpan().getId());
                Assert.assertSame(traceData.getActiveSpan().getMetadata().getValue(Boolean.TRUE.toString()),
                        currentData.getActiveSpan().getMetadata().getValue(Boolean.TRUE.toString()));
                Assert.assertNotSame(traceData, currentData);
            } catch (Throwable t) {
                hasErrors.set(true);
                t.printStackTrace();
            }
            return null;
        });
        future.get();
        Assert.assertSame(traceData, TraceContext.getCurrentTraceData());
        Assert.assertFalse(hasErrors.get());

    }

    @After
    public void after() {
        executorService.shutdownNow();
    }

}
