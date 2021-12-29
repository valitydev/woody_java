package dev.vality.woody.api.event;

import dev.vality.woody.api.trace.ContextSpan;
import dev.vality.woody.api.trace.TraceData;

public class ClientEvent extends Event {

    public ClientEvent(TraceData traceData) {
        super(traceData);
    }

    @Override
    public ClientEventType getEventType() {
        return (ClientEventType) super.getEventType();
    }

    @Override
    public ContextSpan getActiveSpan() {
        return getTraceData().getClientSpan();
    }
}
