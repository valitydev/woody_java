package dev.vality.woody.api.proxy.tracer;

import dev.vality.woody.api.proxy.InstanceMethodCaller;
import dev.vality.woody.api.trace.context.TraceContext;

/**
 * Used to control context lifecycle on interface method call and return
 */
public class ContextTracer implements MethodCallTracer {
    private final TraceContext traceContext;
    private final MethodCallTracer targetTracer;

    public ContextTracer(TraceContext traceContext, MethodCallTracer targetTracer) {
        this.traceContext = traceContext;
        this.targetTracer = targetTracer;
    }

    @Override
    public void beforeCall(Object[] args, InstanceMethodCaller caller) throws Exception {
        traceContext.init();
        targetTracer.beforeCall(args, caller);
    }

    @Override
    public void afterCall(Object[] args, InstanceMethodCaller caller, Object result) throws Exception {
        try {
            targetTracer.afterCall(args, caller, result);
        } finally {
            traceContext.destroy();
        }
    }

    @Override
    public void callError(Object[] args, InstanceMethodCaller caller, Throwable error) throws Exception {
        try {
            targetTracer.callError(args, caller, error);
        } finally {
            traceContext.destroy(true);
        }
    }
}
