package dev.vality.woody.api.event;

import dev.vality.woody.api.flow.error.WErrorDefinition;
import dev.vality.woody.api.trace.*;

public abstract class Event {
    private final TraceData traceData;

    public Event(TraceData traceData) {
        this.traceData = traceData;
    }

    public TraceData getTraceData() {
        return traceData;
    }

    public Object getEventType() {
        return getActiveSpan().getMetadata().getValue(MetadataProperties.EVENT_TYPE);
    }

    public CallType getCallType() {
        return getActiveSpan().getMetadata().getValue(MetadataProperties.CALL_TYPE);
    }

    public String getCallName() {
        return getActiveSpan().getMetadata().getValue(MetadataProperties.CALL_NAME);
    }

    public Object[] getCallArguments() {
        return getActiveSpan().getMetadata().getValue(MetadataProperties.CALL_ARGUMENTS);
    }

    public Object getCallResult() {
        return getActiveSpan().getMetadata().getValue(MetadataProperties.CALL_RESULT);
    }

    public WErrorDefinition getErrorDefinition() {
        return getActiveSpan().getMetadata().getValue(MetadataProperties.ERROR_DEFINITION);
    }

    public String getSpanId() {
        return getActiveSpan().getSpan().getId();
    }

    public String getParentId() {
        return getActiveSpan().getSpan().getParentId();
    }

    public String getOtelTraceId() {
        return getActiveSpan().getSpan().getOtelTraceId();
    }

    public String getOtelSpanId() {
        return getActiveSpan().getSpan().getOtelSpanId();
    }

    public String getTraceId() {
        return getActiveSpan().getSpan().getTraceId();
    }

    public long getTimeStamp() {
        return getActiveSpan().getSpan().getTimestamp();
    }

    public long getDuration() {
        return getActiveSpan().getSpan().getDuration();
    }

    public Endpoint getEndpoint() {
        return getActiveSpan().getMetadata().getValue(MetadataProperties.CALL_ENDPOINT);
    }

    public boolean isSuccessfulCall() {
        return !ContextUtils.hasCallErrors(getActiveSpan());

    }

    public abstract ContextSpan getActiveSpan();
}
