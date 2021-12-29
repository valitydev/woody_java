package dev.vality.woody.api.proxy.tracer;

import dev.vality.woody.api.flow.error.ErrorMapProcessor;
import dev.vality.woody.api.proxy.InstanceMethodCaller;
import dev.vality.woody.api.trace.context.TraceContext;

public class ErrorGenTracer extends EmptyTracer {
    private final ErrorMapProcessor errorProcessor;

    public ErrorGenTracer(ErrorMapProcessor errorProcessor) {
        this.errorProcessor = errorProcessor;
    }

    @Override
    public void afterCall(Object[] args, InstanceMethodCaller caller, Object result) throws Exception {
        process();
    }

    @Override
    public void callError(Object[] args, InstanceMethodCaller caller, Throwable error) throws Exception {
        process();
    }

    private void process() throws Exception {
        Exception ex = errorProcessor.processMapToError(TraceContext.getCurrentTraceData());
        if (ex != null) {
            throw ex;
        }
    }
}
