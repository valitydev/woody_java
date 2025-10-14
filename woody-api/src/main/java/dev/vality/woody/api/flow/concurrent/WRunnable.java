package dev.vality.woody.api.flow.concurrent;

import dev.vality.woody.api.MDCUtils;
import dev.vality.woody.api.generator.ConfiguredSnowflakeIdGenerator;
import dev.vality.woody.api.generator.IdGenerator;
import dev.vality.woody.api.trace.TraceData;
import dev.vality.woody.api.trace.context.TraceContext;

import java.time.Instant;

public class WRunnable implements Runnable {

    private final TraceData traceData;
    private final Runnable wrappedRunnable;
    private final IdGenerator traceIdGenerator;
    private final IdGenerator spanIdGenerator;

    public WRunnable(Runnable runnable, TraceData traceData) {
        this(runnable, traceData, new ConfiguredSnowflakeIdGenerator(), new ConfiguredSnowflakeIdGenerator());
    }

    public WRunnable(Runnable runnable, TraceData traceData,
                      IdGenerator traceIdGenerator, IdGenerator spanIdGenerator) {
        if (runnable == null || traceData == null) {
            throw new NullPointerException("Null arguments're not allowed");
        }
        this.wrappedRunnable = runnable;
        this.traceData = traceData;
        this.traceIdGenerator = traceIdGenerator;
        this.spanIdGenerator = spanIdGenerator;
    }

    public Runnable geWrappedRunnable() {
        return wrappedRunnable;
    }

    public TraceData getTraceData() {
        return traceData;
    }

    @Override
    public void run() {
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
            geWrappedRunnable().run();
            onError = false;
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
