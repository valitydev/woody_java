package dev.vality.woody.api.flow;

import dev.vality.woody.api.flow.concurrent.WCallable;
import dev.vality.woody.api.flow.concurrent.WRunnable;
import dev.vality.woody.api.generator.ConfiguredSnowflakeIdGenerator;
import dev.vality.woody.api.generator.IdGenerator;
import dev.vality.woody.api.trace.TraceData;
import dev.vality.woody.api.trace.context.TraceContext;

import java.util.concurrent.Callable;

public class WFlow {

    private final IdGenerator traceIdGenerator;
    private final IdGenerator spanIdGenerator;

    public WFlow() {
        this(createDefaultIdGenerator());
    }

    public WFlow(IdGenerator idGenerator) {
        this(idGenerator, idGenerator);
    }

    public WFlow(IdGenerator traceIdGenerator, IdGenerator spanIdGenerator) {
        this.traceIdGenerator = traceIdGenerator;
        this.spanIdGenerator = spanIdGenerator;
    }

    public static IdGenerator createDefaultIdGenerator() {
        return new ConfiguredSnowflakeIdGenerator();
    }

    public static WRunnable create(Runnable runnable) {
        IdGenerator idGenerator = createDefaultIdGenerator();
        return new WRunnable(runnable, TraceContext.getCurrentTraceData(), idGenerator, idGenerator);
    }

    public static WRunnable create(Runnable runnable, TraceData traceData) {
        IdGenerator idGenerator = createDefaultIdGenerator();
        return new WRunnable(runnable, traceData, idGenerator, idGenerator);
    }

    public static <T> WCallable<T> create(Callable<T> callable) {
        IdGenerator idGenerator = createDefaultIdGenerator();
        return new WCallable<>(callable, TraceContext.getCurrentTraceData(), idGenerator, idGenerator);
    }

    public static <T> WCallable<T> create(Callable<T> callable, TraceData traceData) {
        IdGenerator idGenerator = createDefaultIdGenerator();
        return new WCallable<>(callable, traceData, idGenerator, idGenerator);
    }

    public static WRunnable createFork(Runnable runnable) {
        return create(runnable, new TraceData());
    }

    public static <T> WCallable<T> createFork(Callable<T> callable) {
        return create(callable, new TraceData());
    }

    public static WRunnable createServiceFork(Runnable runnable, IdGenerator traceIdGenerator,
                                              IdGenerator spanIdGenerator) {
        return new WRunnable(runnable, prepareServiceTraceData(traceIdGenerator, spanIdGenerator),
                traceIdGenerator, spanIdGenerator);
    }

    public static <T> WCallable<T> createServiceFork(Callable<T> callable, IdGenerator traceIdGenerator,
                                                     IdGenerator spanIdGenerator) {
        return new WCallable<>(callable, prepareServiceTraceData(traceIdGenerator, spanIdGenerator),
                traceIdGenerator, spanIdGenerator);
    }

    public WRunnable createServiceFork(Runnable runnable) {
        return createServiceFork(runnable, traceIdGenerator, spanIdGenerator);
    }

    public <T> WCallable<T> createServiceFork(Callable<T> callable) {
        return createServiceFork(callable, traceIdGenerator, spanIdGenerator);
    }

    private static TraceData prepareServiceTraceData(IdGenerator traceIdGenerator, IdGenerator spanIdGenerator) {
        TraceData traceData = TraceContext.initNewServiceTrace(new TraceData(), traceIdGenerator, spanIdGenerator);
        traceData.getServiceSpan().getSpan().setTimestamp(0);
        traceData.getServiceSpan().getSpan().setDuration(0);
        return traceData;
    }
}
