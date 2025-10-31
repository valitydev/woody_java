package dev.vality.woody.api.proxy.tracer;

import dev.vality.woody.api.interceptor.MdcRefreshInterceptor;
import dev.vality.woody.api.proxy.InstanceMethodCaller;
import dev.vality.woody.api.trace.TraceData;
import dev.vality.woody.api.trace.context.TraceContext;

/**
 * Ensures MDC stays in sync around method invocations once metadata is populated.
 */
public class MdcRefreshTracer implements MethodCallTracer {

    @Override
    public void beforeCall(Object[] args, InstanceMethodCaller caller) {
        refresh();
    }

    @Override
    public void afterCall(Object[] args, InstanceMethodCaller caller, Object result) {
        refresh();
    }

    @Override
    public void callError(Object[] args, InstanceMethodCaller caller, Throwable error) {
        refresh();
    }

    private void refresh() {
        TraceData traceData = TraceContext.getCurrentTraceData();
        MdcRefreshInterceptor.refresh(traceData);
    }
}
