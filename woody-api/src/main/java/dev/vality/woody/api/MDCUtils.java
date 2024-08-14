package dev.vality.woody.api;

import dev.vality.woody.api.trace.Span;
import org.slf4j.MDC;

import java.time.Instant;

@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
public class MDCUtils {

    public static final String SPAN_ID = "span_id";
    public static final String OTEL_TRACE_ID = "otel_trace_id";
    public static final String OTEL_SPAN_ID = "otel_span_id";
    public static final String OTEL_TRACE_FLAGS = "otel_trace_flags";
    public static final String TRACE_ID = "trace_id";
    public static final String PARENT_ID = "parent_id";
    public static final String DEADLINE = "deadline";

    /**
     * Put span data in MDC
     *
     * @param span - service or client span
     */
    public static void putSpanData(Span span, io.opentelemetry.api.trace.Span otelSpan) {
        MDC.put(SPAN_ID, span.getId() != null ? span.getId() : "");
        MDC.put(TRACE_ID, span.getTraceId() != null ? span.getTraceId() : "");
        MDC.put(PARENT_ID, span.getParentId() != null ? span.getParentId() : "");
        MDC.put(OTEL_TRACE_ID,
                otelSpan.getSpanContext().getTraceId() != null ? otelSpan.getSpanContext().getTraceId() : "");
        MDC.put(OTEL_SPAN_ID,
                otelSpan.getSpanContext().getSpanId() != null ? otelSpan.getSpanContext().getSpanId() : "");
        MDC.put(OTEL_TRACE_FLAGS,
                otelSpan.getSpanContext().getTraceFlags() != null ? otelSpan.getSpanContext().getTraceFlags().asHex() :
                        "");
        if (span.hasDeadline()) {
            MDC.put(DEADLINE, span.getDeadline().toString());
        }
    }

    /**
     * Remove span data from MDC
     */
    public static void removeSpanData() {
        MDC.remove(SPAN_ID);
        MDC.remove(TRACE_ID);
        MDC.remove(OTEL_TRACE_ID);
        MDC.remove(OTEL_SPAN_ID);
        MDC.remove(OTEL_TRACE_FLAGS);
        MDC.remove(DEADLINE);
    }

    public static void putDeadline(Instant deadline) {
        if (deadline != null) {
            MDC.put(DEADLINE, deadline.toString());
        }
    }

    public static void removeDeadline() {
        MDC.remove(DEADLINE);
    }

}
