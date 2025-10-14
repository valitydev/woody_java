package dev.vality.woody.api.flow.concurrent;

import dev.vality.woody.api.MDCUtils;
import dev.vality.woody.api.generator.ConfiguredSnowflakeIdGenerator;
import dev.vality.woody.api.generator.IdGenerator;
import dev.vality.woody.api.trace.TraceData;
import dev.vality.woody.api.trace.context.TraceContext;

import java.time.Instant;
import java.util.concurrent.Callable;

public class WCallable<T> implements Callable<T> {

    private final TraceData traceData;
    private final Callable<T> wrappedCallable;
    private final IdGenerator traceIdGenerator;
    private final IdGenerator spanIdGenerator;

    public Callable<T> getWrappedCallable() {
        return wrappedCallable;
    }

    public TraceData getTraceData() {
        return traceData;
    }

    public WCallable(Callable<T> wrappedCallable, TraceData traceData) {
        this(wrappedCallable, traceData, new ConfiguredSnowflakeIdGenerator(), new ConfiguredSnowflakeIdGenerator());
    }

    public WCallable(Callable<T> wrappedCallable, TraceData traceData,
                     IdGenerator traceIdGenerator, IdGenerator spanIdGenerator) {
        if (wrappedCallable == null || traceData == null) {
            throw new NullPointerException("Null arguments're not allowed");
        }
        this.traceData = traceData;
        this.wrappedCallable = wrappedCallable;
        this.traceIdGenerator = traceIdGenerator;
        this.spanIdGenerator = spanIdGenerator;
    }

    @Override
    public T call() throws Exception {
        TraceData originalTraceData = TraceContext.getCurrentTraceData();
        TraceData clonedTraceData = getTraceData().cloneObject();
        boolean serviceContext = !clonedTraceData.isClient();
        if (serviceContext) {
            if (clonedTraceData.getClientSpan().isFilled() || clonedTraceData.getClientSpan().getSpan().isStarted()) {
                clonedTraceData.getClientSpan().getSpan().setTraceId(null);
                clonedTraceData.getClientSpan().getSpan().setParentId(null);
                clonedTraceData.getClientSpan().getSpan().setId(null);
                clonedTraceData.getClientSpan().getSpan().setTimestamp(0);
                clonedTraceData.getClientSpan().getSpan().setDuration(0);
                Instant deadline = clonedTraceData.getClientSpan().getSpan().getDeadline();
                if (deadline != null) {
                    clonedTraceData.getClientSpan().getSpan().setDeadline(deadline);
                }
            }
            if (clonedTraceData.getServiceSpan().getSpan().isStarted()) {
                clonedTraceData.clearPreserveOtelSpan();
            } else {
                clonedTraceData.getServiceSpan().getSpan().setTimestamp(0);
                clonedTraceData.getServiceSpan().getSpan().setDuration(0);
                clonedTraceData.clearPreserveOtelSpan();
            }
        } else {
            clonedTraceData.clearPreserveOtelSpan();
        }
        TraceContext.setCurrentTraceData(clonedTraceData);

        TraceContext traceContext = new TraceContext(traceIdGenerator, spanIdGenerator);
        boolean initialized = false;
        boolean onError = true;
        try {
            traceContext.init();
            initialized = true;
            T result = getWrappedCallable().call();
            onError = false;
            return result;
        } finally {
            try {
                if (initialized) {
                    traceContext.destroy(onError);
                }
            } finally {
                TraceContext.setCurrentTraceData(originalTraceData);
                if (originalTraceData != null) {
                    MDCUtils.putTraceData(originalTraceData, originalTraceData.getActiveSpan());
                } else {
                    MDCUtils.removeTraceData();
                }
            }
        }
    }

}
