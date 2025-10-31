package dev.vality.woody.api;

import dev.vality.woody.api.event.CallType;
import dev.vality.woody.api.proxy.InstanceMethodCaller;
import dev.vality.woody.api.trace.*;
import org.slf4j.MDC;

import java.time.Instant;
import java.util.HashSet;
import java.util.Iterator;
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
        if (traceData == null || contextSpan == null || contextSpan.getSpan() == null) {
            removeTraceData();
            return;
        }

        io.opentelemetry.api.trace.Span otelSpan = traceData.getOtelSpan();
        io.opentelemetry.api.trace.SpanContext spanContext = otelSpan != null ? otelSpan.getSpanContext() : null;

        populateSpanIdentifiers(contextSpan.getSpan(), otelSpan);
        populateOtelIdentifiers(spanContext, otelSpan);

        boolean updatingClientSpan = traceData.getClientSpan() == contextSpan;
        boolean updatingServiceSpan = traceData.getServiceSpan() == contextSpan;

        if (isExtendedFieldsEnabled()) {
            if (updatingClientSpan) {
                clearExtendedEntriesWithPrefix(TRACE_RPC_CLIENT_PREFIX, otelSpan);
            }
            if (updatingServiceSpan) {
                clearExtendedEntriesWithPrefix(TRACE_RPC_SERVER_PREFIX, otelSpan);
            }
            if (!updatingClientSpan && !updatingServiceSpan) {
                clearExtendedEntries(false, otelSpan);
            }
            populateExtendedFields(traceData, otelSpan);
        } else {
            clearExtendedEntries(false, otelSpan);
        }

        updateDeadlineEntries(traceData, contextSpan, otelSpan);
    }

    private static void populateSpanIdentifiers(Span span, io.opentelemetry.api.trace.Span otelSpan) {
        putTraceValue(otelSpan, SPAN_ID, span.getId());
        putTraceValue(otelSpan, TRACE_ID, span.getTraceId());
        putTraceValue(otelSpan, PARENT_ID, span.getParentId());
    }

    private static void populateOtelIdentifiers(io.opentelemetry.api.trace.SpanContext spanContext,
                                                io.opentelemetry.api.trace.Span otelSpan) {
        if (spanContext == null) {
            putTraceValue(otelSpan, OTEL_TRACE_ID, null);
            putTraceValue(otelSpan, OTEL_SPAN_ID, null);
            putTraceValue(otelSpan, OTEL_TRACE_FLAGS, null);
            return;
        }
        putTraceValue(otelSpan, OTEL_TRACE_ID, spanContext.getTraceId());
        putTraceValue(otelSpan, OTEL_SPAN_ID, spanContext.getSpanId());
        putTraceValue(otelSpan, OTEL_TRACE_FLAGS,
                spanContext.getTraceFlags() != null ? spanContext.getTraceFlags().asHex() : null);
    }

    public static void removeTraceData() {
        MDC.remove(SPAN_ID);
        MDC.remove(TRACE_ID);
        MDC.remove(OTEL_TRACE_ID);
        MDC.remove(OTEL_SPAN_ID);
        MDC.remove(OTEL_TRACE_FLAGS);
        MDC.remove(DEADLINE);
        clearExtendedEntries(true, null);
    }

    public static void putDeadline(TraceData traceData, ContextSpan contextSpan, Instant deadline) {
        if (deadline == null) {
            removeDeadline(traceData, contextSpan);
            return;
        }

        io.opentelemetry.api.trace.Span otelSpan = traceData != null ? traceData.getOtelSpan() : null;
        updateDeadlineEntries(traceData, contextSpan, otelSpan);
    }

    public static void removeDeadline(TraceData traceData, ContextSpan contextSpan) {
        io.opentelemetry.api.trace.Span otelSpan = traceData != null ? traceData.getOtelSpan() : null;
        updateDeadlineEntries(traceData, contextSpan, otelSpan);
    }

    public static void enableExtendedFields() {
        extendedFieldsEnabled = true;
    }

    public static void disableExtendedFields() {
        extendedFieldsEnabled = false;
        clearExtendedEntries(false, null);
    }

    public static boolean isExtendedFieldsEnabled() {
        return extendedFieldsEnabled;
    }

    private static void populateExtendedFields(TraceData traceData, io.opentelemetry.api.trace.Span otelSpan) {
        addSpanDetails(traceData.getClientSpan(), TRACE_RPC_CLIENT_PREFIX, otelSpan);
        addSpanDetails(traceData.getServiceSpan(), TRACE_RPC_SERVER_PREFIX, otelSpan);
    }

    private static void addSpanDetails(ContextSpan contextSpan, String prefix,
                                       io.opentelemetry.api.trace.Span otelSpan) {
        if (contextSpan == null || !contextSpan.isFilled()) {
            return;
        }

        addExtendedEntry(otelSpan, prefix + "service", resolveServiceName(contextSpan));
        addExtendedEntry(otelSpan, prefix + "function", resolveFunctionName(contextSpan));
        addExtendedEntry(otelSpan, prefix + "type", resolveCallType(contextSpan));
        addExtendedEntry(otelSpan, prefix + "event", resolveEvent(contextSpan));
        addExtendedEntry(otelSpan, prefix + "url", resolveEndpoint(contextSpan));

        long duration = contextSpan.getSpan().getDuration();
        if (duration > 0) {
            addExtendedEntry(otelSpan, prefix + "execution_duration_ms", Long.toString(duration));
        }

        addCustomMetadataEntries(contextSpan, prefix + TRACE_RPC_METADATA_SUFFIX, otelSpan);
    }

    private static void addCustomMetadataEntries(ContextSpan contextSpan, String prefix,
                                                 io.opentelemetry.api.trace.Span otelSpan) {
        Metadata metadata = contextSpan.getCustomMetadata();
        if (metadata == null) {
            return;
        }
        for (String key : metadata.getKeys()) {
            Object value = metadata.getValue(key);
            if (value != null) {
                addExtendedEntry(otelSpan, prefix + key, Objects.toString(value));
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

    private static void addExtendedEntry(io.opentelemetry.api.trace.Span otelSpan, String key, String value) {
        if (key == null || value == null || value.isEmpty()) {
            return;
        }
        putTraceValue(otelSpan, key, value);
        EXTENDED_MDC_KEYS.get().add(key);
    }

    private static void putMdcValue(String key, String value) {
        MDC.put(key, value != null ? value : "");
    }

    public static void removeExtendedEntry(io.opentelemetry.api.trace.Span otelSpan, String key) {
        MDC.remove(key);
        EXTENDED_MDC_KEYS.get().remove(key);
        if (otelSpan != null) {
            otelSpan.setAttribute(key, null);
        }
    }

    private static void updateDeadlineEntries(TraceData traceData, ContextSpan contextSpan,
                                              io.opentelemetry.api.trace.Span otelSpan) {
        Instant activeDeadline = contextSpan != null ? ContextUtils.getDeadline(contextSpan) : null;
        if (activeDeadline != null) {
            putTraceValue(otelSpan, DEADLINE, activeDeadline.toString());
        } else {
            MDC.remove(DEADLINE);
            if (otelSpan != null) {
                otelSpan.setAttribute(DEADLINE, null);
            }
        }

        boolean updatingClientSpan = traceData != null && traceData.getClientSpan() == contextSpan;
        boolean updatingServiceSpan = traceData != null && traceData.getServiceSpan() == contextSpan;

        if (!isExtendedFieldsEnabled()) {
            if (updatingClientSpan || (!updatingClientSpan && !updatingServiceSpan)) {
                removeExtendedEntry(otelSpan, TRACE_RPC_CLIENT_PREFIX + "deadline");
            }
            if (updatingServiceSpan || (!updatingClientSpan && !updatingServiceSpan)) {
                removeExtendedEntry(otelSpan, TRACE_RPC_SERVER_PREFIX + "deadline");
            }
            return;
        }

        if (traceData != null) {
            if (updatingClientSpan) {
                removeExtendedEntry(otelSpan, TRACE_RPC_CLIENT_PREFIX + "deadline");
            }
            if (updatingServiceSpan) {
                removeExtendedEntry(otelSpan, TRACE_RPC_SERVER_PREFIX + "deadline");
            }
            if (!updatingClientSpan && !updatingServiceSpan) {
                removeExtendedEntry(otelSpan, TRACE_RPC_CLIENT_PREFIX + "deadline");
                removeExtendedEntry(otelSpan, TRACE_RPC_SERVER_PREFIX + "deadline");
            }
            addDeadlineEntry(traceData.getClientSpan(), TRACE_RPC_CLIENT_PREFIX, otelSpan);
            addDeadlineEntry(traceData.getServiceSpan(), TRACE_RPC_SERVER_PREFIX, otelSpan);
        }
    }

    private static void addDeadlineEntry(ContextSpan span, String prefix, io.opentelemetry.api.trace.Span otelSpan) {
        if (span == null) {
            return;
        }
        Instant deadline = ContextUtils.getDeadline(span);
        if (deadline != null) {
            addExtendedEntry(otelSpan, prefix + "deadline", deadline.toString());
        }
    }

    private static void putTraceValue(io.opentelemetry.api.trace.Span otelSpan, String key, String value) {
        putMdcValue(key, value);
        if (otelSpan != null) {
            otelSpan.setAttribute(key, value != null ? value : "");
        }
    }

    private static void clearExtendedEntries(boolean removeThreadLocal, io.opentelemetry.api.trace.Span otelSpan) {
        Set<String> keys = EXTENDED_MDC_KEYS.get();
        for (String key : keys) {
            MDC.remove(key);
            if (otelSpan != null) {
                otelSpan.setAttribute(key, null);
            }
        }

        if (removeThreadLocal) {
            EXTENDED_MDC_KEYS.remove();
        } else {
            keys.clear();
        }
    }

    private static void clearExtendedEntriesWithPrefix(String prefix,
                                                       io.opentelemetry.api.trace.Span otelSpan) {
        Set<String> keys = EXTENDED_MDC_KEYS.get();
        Iterator<String> iterator = keys.iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            if (key.startsWith(prefix)) {
                MDC.remove(key);
                if (otelSpan != null) {
                    otelSpan.setAttribute(key, null);
                }
                iterator.remove();
            }
        }
    }
}
