package dev.vality.woody.api.flow.concurrent;

import dev.vality.woody.api.MDCUtils;
import dev.vality.woody.api.trace.TraceData;
import dev.vality.woody.api.trace.context.TraceContext;

public class WRunnable implements Runnable {

    private final TraceData traceData;
    private final Runnable wrappedRunnable;

    public WRunnable(Runnable runnable, TraceData traceData) {
        if (runnable == null || traceData == null) {
            throw new NullPointerException("Null arguments're not allowed");
        }
        this.wrappedRunnable = runnable;
        this.traceData = traceData;
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
        TraceContext.setCurrentTraceData(getTraceData().cloneObject());

        if (traceData != originalTraceData) {
            MDCUtils.putTraceData(traceData, traceData.getActiveSpan());
        }

        try {
            geWrappedRunnable().run();
        } finally {
            TraceContext.setCurrentTraceData(originalTraceData);
            MDCUtils.putTraceData(originalTraceData, originalTraceData.getActiveSpan());
        }
    }
}
