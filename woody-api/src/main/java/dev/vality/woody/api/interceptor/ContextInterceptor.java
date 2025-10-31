package dev.vality.woody.api.interceptor;

import dev.vality.woody.api.MDCUtils;
import dev.vality.woody.api.trace.ContextUtils;
import dev.vality.woody.api.trace.TraceData;
import dev.vality.woody.api.trace.context.TraceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class ContextInterceptor implements CommonInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(ContextInterceptor.class);

    private final TraceContext traceContext;
    private final CommonInterceptor interceptor;
    private final ThreadLocal<Boolean> contextInitialized = ThreadLocal.withInitial(() -> Boolean.FALSE);

    public ContextInterceptor(TraceContext traceContext, CommonInterceptor interceptor) {
        this.traceContext = Objects.requireNonNull(traceContext, "TraceContext can't be null");
        this.interceptor = interceptor != null ? interceptor : new EmptyCommonInterceptor();
    }

    @Override
    public boolean interceptRequest(TraceData traceData, Object providerContext, Object... contextParams) {
        LOG.trace("Intercept request context");
        boolean spanFilled = traceData != null && traceData.getServiceSpan().isFilled();
        if (spanFilled) {
            traceContext.init();
        } else {
            LOG.trace("Skipping trace context init due to empty service span");
        }
        contextInitialized.set(spanFilled);
        return interceptor.interceptRequest(traceData, providerContext, contextParams);
    }

    @Override
    public boolean interceptResponse(TraceData traceData, Object providerContext, Object... contextParams) {
        LOG.trace("Intercept response context");
        try {
            return interceptor.interceptResponse(traceData, providerContext, contextParams);
        } finally {
            Boolean initialized = contextInitialized.get();
            try {
                if (Boolean.TRUE.equals(initialized)
                        && traceData != null
                        && traceData.getServiceSpan().isFilled()) {
                    traceContext.destroy(ContextUtils.hasCallErrors(traceData.getActiveSpan()));
                } else {
                    TraceContext.reset();
                    MDCUtils.removeTraceData();
                }
            } finally {
                contextInitialized.remove();
            }
        }
    }
}
