package dev.vality.woody.api.flow;

import dev.vality.woody.api.flow.concurrent.WCallable;
import dev.vality.woody.api.flow.concurrent.WRunnable;
import dev.vality.woody.api.generator.ConfiguredSnowflakeIdGenerator;
import dev.vality.woody.api.generator.IdGenerator;
import dev.vality.woody.api.trace.TraceData;
import dev.vality.woody.api.trace.context.TraceContext;
import io.opentelemetry.sdk.resources.Resource;

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
        return new WRunnable(runnable, TraceContext.getCurrentTraceData());
    }

    public static WRunnable create(Runnable runnable, TraceData traceData) {
        return new WRunnable(runnable, traceData);
    }

    public static <T> WCallable<T> create(Callable<T> callable) {
        return new WCallable<>(callable, TraceContext.getCurrentTraceData());
    }

    public static <T> WCallable<T> create(Callable<T> callable, TraceData traceData) {
        return new WCallable<>(callable, traceData);
    }

    public static WRunnable createFork(Runnable runnable, String resource) {
        return create(runnable, new TraceData(resource));
    }

    public static <T> WCallable<T> createFork(Callable<T> callable, String resource) {
        return create(callable, new TraceData(resource));
    }

    public static WRunnable createServiceFork(Runnable runnable, IdGenerator idGenerator, String resource) {
        return create(runnable, TraceContext.initNewServiceTrace(new TraceData(resource), idGenerator, idGenerator));
    }

    public static WRunnable createServiceFork(Runnable runnable, IdGenerator traceIdGenerator,
                                              IdGenerator spanIdGenerator, String resource) {
        return create(runnable,
                TraceContext.initNewServiceTrace(new TraceData(resource), traceIdGenerator, spanIdGenerator));
    }

    public static <T> WCallable<T> createServiceFork(Callable<T> callable, IdGenerator idGenerator, String resource) {
        return create(callable, TraceContext.initNewServiceTrace(new TraceData(resource), idGenerator, idGenerator));
    }

    public static <T> WCallable<T> createServiceFork(Callable<T> callable, IdGenerator traceIdGenerator,
                                                     IdGenerator spanIdGenerator, String resource) {
        return create(callable,
                TraceContext.initNewServiceTrace(new TraceData(resource), traceIdGenerator, spanIdGenerator));
    }

    public WRunnable createServiceFork(Runnable runnable, String resource) {
        return createServiceFork(runnable, traceIdGenerator, spanIdGenerator, resource);
    }

    public <T> WCallable<T> createServiceFork(Callable<T> callable, String resource) {
        return createServiceFork(callable, traceIdGenerator, spanIdGenerator, resource);
    }

}
