package dev.vality.woody.thrift.impl.http.transport;

public enum THttpHeader {
    TRACE_ID("woody.trace-id", false),
    SPAN_ID("woody.span-id", false),
    TRACE_PARENT("traceparent", true),
    TRACE_STATE("tracestate", true),
    PARENT_ID("woody.parent-id", false),
    DEADLINE("woody.deadline", false),
    ERROR_CLASS("woody.error-class", false),
    ERROR_REASON("woody.error-reason", false),
    META("woody.meta.", false);

    private final String key;
    private final boolean optional;

    THttpHeader(String key, boolean optional) {
        this.key = key;
        this.optional = optional;
    }

    public String getKey() {
        return key;
    }

    public boolean isOptional() {
        return optional;
    }
}
