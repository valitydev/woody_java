package dev.vality.woody.api.proxy.tracer;

import dev.vality.woody.api.MDCUtils;
import dev.vality.woody.api.event.ClientEventType;
import dev.vality.woody.api.event.ServiceEventType;
import dev.vality.woody.api.proxy.InstanceMethodCaller;
import dev.vality.woody.api.trace.ContextSpan;
import dev.vality.woody.api.trace.ContextUtils;
import dev.vality.woody.api.trace.Metadata;
import dev.vality.woody.api.trace.MetadataProperties;
import dev.vality.woody.api.trace.TraceData;
import dev.vality.woody.api.trace.context.TraceContext;

public class TargetCallTracer implements MethodCallTracer {
    private final boolean isClient;
    private final boolean isAuto;

    public TargetCallTracer() {
        this.isAuto = true;
        this.isClient = false;
    }

    public TargetCallTracer(boolean isClient) {
        this.isAuto = false;
        this.isClient = isClient;
    }

    public static TargetCallTracer forClient() {
        return new TargetCallTracer(true);
    }

    public static TargetCallTracer forServer() {
        return new TargetCallTracer(false);
    }

    public static TargetCallTracer forAuto() {
        return new TargetCallTracer();
    }

    @Override
    public void beforeCall(Object[] args, InstanceMethodCaller caller) {
        boolean isClient = isClient();
        setBeforeCall(isClient ? TraceContext.getCurrentTraceData().getClientSpan().getMetadata() :
                TraceContext.getCurrentTraceData().getServiceSpan().getMetadata(), args, caller, isClient);

    }

    @Override
    public void afterCall(Object[] args, InstanceMethodCaller caller, Object result) {
        boolean isClient = isClient();
        setAfterCall(isClient ? TraceContext.getCurrentTraceData().getClientSpan().getMetadata() :
                TraceContext.getCurrentTraceData().getServiceSpan().getMetadata(), args, caller, result, isClient);
    }

    @Override
    public void callError(Object[] args, InstanceMethodCaller caller, Throwable error) {
        boolean isClient = isClient();
        setCallError(isClient ? TraceContext.getCurrentTraceData().getClientSpan() :
                TraceContext.getCurrentTraceData().getServiceSpan(), args, caller, error, isClient);
    }

    private void setBeforeCall(Metadata metadata, Object[] args, InstanceMethodCaller caller, boolean isClient) {
        metadata.putValue(MetadataProperties.CALL_ARGUMENTS, args);
        metadata.putValue(MetadataProperties.INSTANCE_METHOD_CALLER, caller);
        metadata.putValue(MetadataProperties.EVENT_TYPE,
                isClient ? ClientEventType.CALL_SERVICE : ServiceEventType.CALL_HANDLER);
        TraceData currentTraceData = TraceContext.getCurrentTraceData();
        ContextSpan activeSpan = currentTraceData != null ? currentTraceData.getActiveSpan() : null;
        if (currentTraceData != null && activeSpan != null) {
            MDCUtils.putTraceData(currentTraceData, activeSpan);
        }
    }

    private void setAfterCall(Metadata metadata, Object[] args, InstanceMethodCaller caller, Object result,
                              boolean isClient) {
        metadata.putValue(MetadataProperties.CALL_RESULT, result);
        metadata.putValue(MetadataProperties.EVENT_TYPE,
                isClient ? ClientEventType.SERVICE_RESULT : ServiceEventType.HANDLER_RESULT);
    }

    private void setCallError(ContextSpan contextSpan, Object[] args, InstanceMethodCaller caller, Throwable error,
                              boolean isClient) {
        ContextUtils.setCallError(contextSpan, error);
        contextSpan.getMetadata()
                .putValue(MetadataProperties.EVENT_TYPE, isClient ? ClientEventType.ERROR : ServiceEventType.ERROR);
    }

    private boolean isClient() {
        return isAuto ? TraceContext.getCurrentTraceData().isClient() : isClient;
    }
}
