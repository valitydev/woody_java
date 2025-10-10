package dev.vality.woody.api;

import dev.vality.woody.api.event.CallType;
import dev.vality.woody.api.proxy.InstanceMethodCaller;
import dev.vality.woody.api.trace.*;
import org.slf4j.MDC;

import java.time.Instant;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
public class MDCUtils {

    public static final String SPAN_ID = "span_id";
    public static final String OTEL_TRACE_ID = "otel_trace_id";
    public static final String OTEL_SPAN_ID = "otel_span_id";
    public static final String OTEL_TRACE_FLAGS = "otel_trace_flags";
    public static final String TRACE_ID = "trace_id";
    public static final String PARENT_ID = "parent_id";
    public static final String DEADLINE = "deadline";
    public static final String TRACE_RPC_PREFIX = "rpc.";
    public static final String TRACE_RPC_CLIENT_PREFIX = TRACE_RPC_PREFIX + "client.";
    public static final String TRACE_RPC_SERVER_PREFIX = TRACE_RPC_PREFIX + "server.";
    public static final String TRACE_RPC_METADATA_SUFFIX = "metadata.";
    public static final String EXTENDED_MDC_PROPERTY = "woody.mdc.extended";
    private static final ThreadLocal<Set<String>> EXTENDED_MDC_KEYS = ThreadLocal.withInitial(HashSet::new);
    private static volatile boolean extendedFieldsEnabled =
            Boolean.parseBoolean(System.getProperty(EXTENDED_MDC_PROPERTY, "true"));

    public static void putTraceData(TraceData traceData, ContextSpan contextSpan) {
        if (traceData == null || contextSpan == null) {
            removeTraceData();
            return;
        }

        Span span = contextSpan.getSpan();
        if (span == null) {
            removeTraceData();
            return;
        }

        io.opentelemetry.api.trace.Span otelSpan = traceData.getOtelSpan();
        io.opentelemetry.api.trace.SpanContext spanContext = otelSpan != null ? otelSpan.getSpanContext() : null;

        MDC.put(SPAN_ID, span.getId() != null ? span.getId() : "");
        MDC.put(TRACE_ID, span.getTraceId() != null ? span.getTraceId() : "");
        MDC.put(PARENT_ID, span.getParentId() != null ? span.getParentId() : "");
        MDC.put(OTEL_TRACE_ID,
                spanContext != null && spanContext.getTraceId() != null ? spanContext.getTraceId() : "");
        MDC.put(OTEL_SPAN_ID,
                spanContext != null && spanContext.getSpanId() != null ? spanContext.getSpanId() : "");
        MDC.put(OTEL_TRACE_FLAGS,
                spanContext != null && spanContext.getTraceFlags() != null
                        ? spanContext.getTraceFlags().asHex()
                        : "");

        clearExtendedEntries(false);
        if (isExtendedFieldsEnabled()) {
            populateExtendedFields(traceData);
        }

        updateDeadlineEntries(traceData, contextSpan);
    }

    public static void removeTraceData() {
        MDC.remove(SPAN_ID);
        MDC.remove(TRACE_ID);
        MDC.remove(OTEL_TRACE_ID);
        MDC.remove(OTEL_SPAN_ID);
        MDC.remove(OTEL_TRACE_FLAGS);
        MDC.remove(DEADLINE);
        clearExtendedEntries(true);
    }

    public static void putDeadline(TraceData traceData, ContextSpan contextSpan, Instant deadline) {
        if (deadline == null) {
            removeDeadline(traceData, contextSpan);
            return;
        }

        updateDeadlineEntries(traceData, contextSpan);
    }

    public static void removeDeadline(TraceData traceData, ContextSpan contextSpan) {
        updateDeadlineEntries(traceData, contextSpan);
    }

    public static void setExtendedFieldsEnabled(boolean enabled) {
        extendedFieldsEnabled = enabled;
        if (!enabled) {
            clearExtendedEntries(false);
        }
    }

    public static boolean isExtendedFieldsEnabled() {
        return extendedFieldsEnabled;
    }

    private static void populateExtendedFields(TraceData traceData) {
        addSpanDetails(traceData.getClientSpan(), TRACE_RPC_CLIENT_PREFIX);
        addSpanDetails(traceData.getServiceSpan(), TRACE_RPC_SERVER_PREFIX);
    }

    private static void addSpanDetails(ContextSpan contextSpan, String prefix) {
        if (contextSpan == null || !contextSpan.isFilled()) {
            return;
        }

        addExtendedEntry(prefix + "service", resolveServiceName(contextSpan));
        addExtendedEntry(prefix + "function", resolveFunctionName(contextSpan));
        addExtendedEntry(prefix + "type", resolveCallType(contextSpan));
        addExtendedEntry(prefix + "event", resolveEvent(contextSpan));
        addExtendedEntry(prefix + "url", resolveEndpoint(contextSpan));

        long duration = contextSpan.getSpan().getDuration();
        if (duration > 0) {
            addExtendedEntry(prefix + "execution_duration_ms", Long.toString(duration));
        }

        addCustomMetadataEntries(contextSpan, prefix + TRACE_RPC_METADATA_SUFFIX);
    }

    private static void addCustomMetadataEntries(ContextSpan contextSpan, String prefix) {
        Metadata metadata = contextSpan.getCustomMetadata();
        if (metadata == null) {
            return;
        }
        for (String key : metadata.getKeys()) {
            Object value = metadata.getValue(key);
            if (value != null) {
                addExtendedEntry(prefix + key, Objects.toString(value));
            }
        }
    }

    private static String resolveServiceName(ContextSpan contextSpan) {
        InstanceMethodCaller caller = contextSpan.getMetadata().getValue(MetadataProperties.INSTANCE_METHOD_CALLER);
        if (caller == null || caller.getTargetMethod() == null) {
            return null;
        }
        Class<?> declaringClass = caller.getTargetMethod().getDeclaringClass();
        if (declaringClass == null) {
            return null;
        }
        Class<?> serviceClass = declaringClass;
        if (declaringClass.getEnclosingClass() != null) {
            String simple = declaringClass.getSimpleName();
            if ("Iface".equals(simple) || "AsyncIface".equals(simple)) {
                serviceClass = declaringClass.getEnclosingClass();
            }
        }
        String simpleName = serviceClass.getSimpleName();
        if (simpleName.endsWith("Srv")) {
            simpleName = simpleName.substring(0, simpleName.length() - 3);
        }
        return simpleName;
    }

    private static String resolveFunctionName(ContextSpan contextSpan) {
        String callName = contextSpan.getMetadata().getValue(MetadataProperties.CALL_NAME);
        if (callName != null && !callName.isEmpty()) {
            return callName;
        }
        InstanceMethodCaller caller = contextSpan.getMetadata().getValue(MetadataProperties.INSTANCE_METHOD_CALLER);
        if (caller != null && caller.getTargetMethod() != null) {
            return caller.getTargetMethod().getName();
        }
        return null;
    }

    private static String resolveCallType(ContextSpan contextSpan) {
        CallType callType = contextSpan.getMetadata().getValue(MetadataProperties.CALL_TYPE);
        if (callType != null) {
            return callType.name().toLowerCase(Locale.ROOT);
        }
        return null;
    }

    private static String resolveEvent(ContextSpan contextSpan) {
        Object event = contextSpan.getMetadata().getValue(MetadataProperties.EVENT_TYPE);
        if (event instanceof Enum<?>) {
            return formatEnum((Enum<?>) event);
        }
        return null;
    }

    private static String resolveEndpoint(ContextSpan contextSpan) {
        Object endpoint = contextSpan.getMetadata().getValue(MetadataProperties.CALL_ENDPOINT);
        if (endpoint instanceof Endpoint) {
            return ((Endpoint<?>) endpoint).getStringValue();
        }
        return endpoint != null ? endpoint.toString() : null;
    }

    private static String formatEnum(Enum<?> value) {
        return value == null ? null : value.name().toLowerCase(Locale.ROOT).replace('_', ' ');
    }

    private static void addExtendedEntry(String key, String value) {
        if (key == null || value == null || value.isEmpty()) {
            return;
        }
        MDC.put(key, value);
        EXTENDED_MDC_KEYS.get().add(key);
    }

    private static void removeExtendedEntry(String key) {
        MDC.remove(key);
        EXTENDED_MDC_KEYS.get().remove(key);
    }

    private static void updateDeadlineEntries(TraceData traceData, ContextSpan contextSpan) {
        Instant activeDeadline = contextSpan != null ? ContextUtils.getDeadline(contextSpan) : null;
        if (activeDeadline != null) {
            MDC.put(DEADLINE, activeDeadline.toString());
        } else {
            MDC.remove(DEADLINE);
        }

        removeExtendedEntry(TRACE_RPC_CLIENT_PREFIX + "deadline");
        removeExtendedEntry(TRACE_RPC_SERVER_PREFIX + "deadline");

        if (!isExtendedFieldsEnabled()) {
            return;
        }

        if (traceData != null) {
            addDeadlineEntry(traceData.getClientSpan(), TRACE_RPC_CLIENT_PREFIX);
            addDeadlineEntry(traceData.getServiceSpan(), TRACE_RPC_SERVER_PREFIX);
        }
    }

    private static void addDeadlineEntry(ContextSpan span, String prefix) {
        if (span == null) {
            return;
        }
        Instant deadline = ContextUtils.getDeadline(span);
        if (deadline != null) {
            addExtendedEntry(prefix + "deadline", deadline.toString());
        }
    }

    private static void clearExtendedEntries(boolean removeThreadLocal) {
        Set<String> keys = EXTENDED_MDC_KEYS.get();
        for (String key : keys) {
            MDC.remove(key);
        }
        keys.clear();
        if (removeThreadLocal) {
            EXTENDED_MDC_KEYS.remove();
        }
    }
}
