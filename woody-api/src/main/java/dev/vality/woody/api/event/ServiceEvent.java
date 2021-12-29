package dev.vality.woody.api.event;

import dev.vality.woody.api.trace.ContextSpan;
import dev.vality.woody.api.trace.TraceData;

public class ServiceEvent extends Event {
    public ServiceEvent(TraceData traceData) {
        super(traceData);
    }

    @Override
    public ServiceEventType getEventType() {
        return (ServiceEventType) super.getEventType();
    }

    @Override
    public ContextSpan getActiveSpan() {
        return getTraceData().getServiceSpan();
    }
}
