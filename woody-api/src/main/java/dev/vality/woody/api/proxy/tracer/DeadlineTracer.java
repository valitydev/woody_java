package dev.vality.woody.api.proxy.tracer;

import dev.vality.woody.api.MDCUtils;
import dev.vality.woody.api.flow.error.WUnavailableResultException;
import dev.vality.woody.api.proxy.InstanceMethodCaller;
import dev.vality.woody.api.trace.ContextSpan;
import dev.vality.woody.api.trace.ContextUtils;
import dev.vality.woody.api.trace.TraceData;
import dev.vality.woody.api.trace.context.TraceContext;

import java.time.Instant;

public class DeadlineTracer extends EmptyTracer {

    public static DeadlineTracer forClient(int networkTimeout) {
        return new DeadlineTracer(true, networkTimeout);
    }

    public static DeadlineTracer forService() {
        return new DeadlineTracer(false);
    }

    private final boolean isClient;

    private final int networkTimeout;

    private DeadlineTracer(boolean isClient) {
        this.isClient = isClient;
        this.networkTimeout = -1;
    }

    private DeadlineTracer(boolean isClient, Integer networkTimeout) {
        this.isClient = isClient;
        this.networkTimeout = networkTimeout;
    }

    @Override
    public void beforeCall(Object[] args, InstanceMethodCaller caller) throws Exception {
        TraceData traceData = TraceContext.getCurrentTraceData();
        ContextSpan contextSpan = traceData.getActiveSpan();
        Instant deadline = ContextUtils.getDeadline(contextSpan);
        if (deadline != null) {
            validateDeadline(deadline);
        } else {
            if (isClient && networkTimeout > 0) {
                deadline = Instant.now().plusMillis(networkTimeout);
                ContextUtils.setDeadline(contextSpan, deadline);
                MDCUtils.putDeadline(traceData, contextSpan, deadline);
            }
        }
    }

    @Override
    public void afterCall(Object[] args, InstanceMethodCaller caller, Object result) throws Exception {
        TraceData traceData = TraceContext.getCurrentTraceData();
        ContextSpan contextSpan = traceData.getActiveSpan();
        if (ContextUtils.getDeadline(contextSpan) == null) {
            MDCUtils.removeDeadline(traceData, contextSpan);
        }
    }

    private void validateDeadline(Instant deadline) {
        if (deadline.isBefore(Instant.now())) {
            throw new WUnavailableResultException("deadline reached");
        }
    }

}
